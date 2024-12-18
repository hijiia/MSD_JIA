window.onload = function() {
    const canvas = document.getElementById('gameCanvas');
    const ctx = canvas.getContext('2d');


    let mouseX = 0, mouseY = 0;


    const playerImage = new Image();
    playerImage.src = 'player.png';

    // const followerImage = new Image();
    // followerImage.src = 'follower.png';
    // end game


    const player = { x: 300, y: 200, width: 50, height: 50 };
    const followers = [
        { x: 100, y: 100, width: 50, height: 50 },
        { x: 200, y: 150, width: 50, height: 50 },
        { x: 150, y: 250, width: 50, height: 50 }
    ];


    document.addEventListener('mousemove', function(event) {
        const rect = canvas.getBoundingClientRect();
        mouseX = event.clientX - rect.left;
        mouseY = event.clientY - rect.top;
    });


    function update() {
        // Move player towards the mouse
        player.x += (mouseX - player.x - player.width / 2) * 0.05;
        player.y += (mouseY - player.y - player.height / 2) * 0.05;

        // Move followers towards the player
        followers.forEach(follower => {
            follower.x += (player.x - follower.x) * 0.02;
            follower.y += (player.y - follower.y) * 0.02;
        });
    }


    function draw() {
        ctx.clearRect(0, 0, canvas.width, canvas.height);


        ctx.drawImage(playerImage, player.x, player.y, player.width, player.height);


        followers.forEach(follower => {
            ctx.drawImage(followerImage, follower.x, follower.y, follower.width, follower.height);
        });
    }


    function gameLoop() {
        update();
        draw();
        requestAnimationFrame(gameLoop);
    }

    playerImage.onload = function() {
        gameLoop();
    };
};