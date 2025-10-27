const express = require('express');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcrypt');
const multer = require('multer');
const { v4: uuidv4 } = require('uuid');
const Database = require('better-sqlite3');
const fs = require('fs');
const path = require('path');
const cors = require('cors');

const JWT_SECRET = process.env.JWT_SECRET || 'change-me';
const UPLOAD_ROOT = path.join(__dirname, 'uploads');
fs.mkdirSync(UPLOAD_ROOT, { recursive: true });

const db = new Database('app.db');
db.exec(`
CREATE TABLE IF NOT EXISTS users(
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  email TEXT UNIQUE NOT NULL,
  password_hash TEXT NOT NULL
);
CREATE TABLE IF NOT EXISTS photos(
  id TEXT PRIMARY KEY,
  user_id INTEGER NOT NULL,
  file_path TEXT NOT NULL,
  mime_type TEXT NOT NULL,
  size INTEGER NOT NULL,
  created_at TEXT NOT NULL,
  FOREIGN KEY(user_id) REFERENCES users(id)
);
`);

const app = express();
app.use(cors());
app.use(express.json());

// --- auth helpers ---
function makeToken(user) {
  return jwt.sign({ uid: user.id, email: user.email }, JWT_SECRET, { expiresIn: '7d' });
}
function auth(req, res, next) {
  const h = req.headers.authorization || '';
  const token = h.startsWith('Bearer ') ? h.slice(7) : null;
  if (!token) return res.status(401).json({ error: 'no token' });
  try { req.user = jwt.verify(token, JWT_SECRET); next(); }
  catch { return res.status(401).json({ error: 'bad token' }); }
}

// --- auth routes ---
app.post('/auth/register', async (req, res) => {
  const { email, password } = req.body || {};
  if (!email || !password) return res.status(400).json({ error: 'email & password required' });
  const hash = await bcrypt.hash(password, 12);
  try {
    const info = db.prepare('INSERT INTO users(email, password_hash) VALUES (?,?)').run(email, hash);
    const user = { id: info.lastInsertRowid, email };
    return res.json({ token: makeToken(user) });
  } catch (e) {
    if (e.code === 'SQLITE_CONSTRAINT_UNIQUE') return res.status(409).json({ error: 'email exists' });
    return res.status(500).json({ error: 'server' });
  }
});

app.post('/auth/login', async (req, res) => {
  const { email, password } = req.body || {};
  const row = db.prepare('SELECT * FROM users WHERE email = ?').get(email);
  if (!row) return res.status(401).json({ error: 'invalid' });
  const ok = await bcrypt.compare(password, row.password_hash);
  if (!ok) return res.status(401).json({ error: 'invalid' });
  res.json({ token: makeToken(row) });
});

// --- upload setup (per-user folder) ---
const storage = multer.diskStorage({
  destination: (req, file, cb) => {
    const userDir = path.join(UPLOAD_ROOT, String(req.user.uid));
    fs.mkdirSync(userDir, { recursive: true });
    cb(null, userDir);
  },
  filename: (req, file, cb) => cb(null, uuidv4() + path.extname(file.originalname || '.jpg'))
});
const upload = multer({
  storage,
  limits: { fileSize: 10 * 1024 * 1024 }, // 10MB
  fileFilter: (_req, file, cb) => {
    if (/^image\/(jpeg|png|webp)$/.test(file.mimetype)) cb(null, true);
    else cb(new Error('Only jpeg/png/webp'));
  }
});

// --- photos ---
app.post('/photos', auth, upload.single('file'), (req, res) => {
  const id = uuidv4();
  const now = new Date().toISOString();
  db.prepare(
    'INSERT INTO photos(id, user_id, file_path, mime_type, size, created_at) VALUES (?,?,?,?,?,?)'
  ).run(id, req.user.uid, req.file.path, req.file.mimetype, req.file.size, now);
  res.json({ id, createdAt: now });
});

app.get('/photos', auth, (req, res) => {
  const rows = db.prepare(
    'SELECT id, mime_type as mimeType, size, created_at as createdAt FROM photos WHERE user_id = ? ORDER BY created_at DESC'
  ).all(req.user.uid);
  res.json(rows);
});

app.get('/photos/:id',st auth, (req, res) => {
         con row = db.prepare('SELECT * FROM photos WHERE id = ?').get(req.params.id);
  if (!row || row.user_id !== req.user.uid) return res.status(404).json({ error: 'not found' });
  res.setHeader('Content-Type', row.mime_type);
  fs.createReadStream(row.file_path).pipe(res);
});

app.delete('/photos/:id', auth, (req, res) => {
  const row = db.prepare('SELECT * FROM photos WHERE id = ?').get(req.params.id);
  if (!row || row.user_id !== req.user.uid) return res.status(404).json({ error: 'not found' });
  try { fs.unlinkSync(row.file_path); } catch {}
  db.prepare('DELETE FROM photos WHERE id = ?').run(req.params.id);
  res.json({ ok: true });
});

app.listen(3000, () => console.log('API on http://localhost:3000'));