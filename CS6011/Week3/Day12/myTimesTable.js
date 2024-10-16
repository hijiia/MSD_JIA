function createTimesTable() {
    const table = document.createElement('table');

    for (let i = 1; i <= 10; i++) {
        const tr = document.createElement('tr');
        for (let j = 1; j <= 10; j++) {
            const td = document.createElement('td');
            td.textContent = i * j;
            tr.appendChild(td);
        }
        table.appendChild(tr);
    }

    document.body.appendChild(table);
}

function addInteractivity() {
    let lastClickedCell = null;

    const cells = document.querySelectorAll('td');
    cells.forEach(cell => {
        // highlight
        cell.addEventListener('mouseenter', () => {
            cell.classList.add('highlighted');
        });

        // remove highlight
        cell.addEventListener('mouseleave', () => {
            cell.classList.remove('highlighted');
        });

        //
        cell.addEventListener('click', () => {
            if (lastClickedCell) {
                lastClickedCell.classList.remove('clicked');
            }
            cell.classList.add('clicked');
            lastClickedCell = cell;
        });
    });
}

let colorInterval;
function animateBackgroundColor() {
    let hue = 0;
    colorInterval = setInterval(() => {
        document.body.style.backgroundColor = `hsl(${hue}, 100%, 50%)`;
        hue = (hue + 1) % 360;
    }, 100);
}

function toggleBackgroundColor() {
    const toggleText = document.createElement('p');
    toggleText.textContent = 'Click here to toggle background color.';
    toggleText.style.cursor = 'pointer';
    document.body.appendChild(toggleText);

    let isAnimating = true;

    toggleText.addEventListener('click', () => {
        if (isAnimating) {
            clearInterval(colorInterval);
        } else {
            animateBackgroundColor();
        }
        isAnimating = !isAnimating;
    });
}

function init() {
    createTimesTable();
    addInteractivity();
    animateBackgroundColor();
    toggleBackgroundColor();
}

window.onload = init;