
#include <SFML/Graphics.hpp>
#include <SFML/System.hpp>
#include <SFML/Audio.hpp>
#include <iostream>
#include <memory>
#include <vector>

void renderObjects(const std::vector<sf::Drawable*>& objects, sf::RenderWindow& window) {
    for (auto object : objects) {
        window.draw(*object);
    }
}

int main() {
    // Create the window
    sf::RenderWindow window(sf::VideoMode(1950, 1118), "Capybara Love Carrots");
    
    // Load background music
    sf::Music backgroundMusic;
    if (!backgroundMusic.openFromFile("cutemusic.ogg")) {
        std::cerr << "Error loading background music!" << std::endl;
        return -1;
    }
    backgroundMusic.setLoop(true); // Loop the music
    backgroundMusic.play(); // Start playing the music
    
    // Hold the drawable objects in a vector to use them in the draw function
    std::vector<sf::Drawable*> gameObjectsDrawable;
    
    // Holding the textures for the sprites
    std::vector<std::unique_ptr<sf::Texture>> textures;
    
    // Load the texture frames
    std::vector<std::string> frameFiles = {
        "capybara/capybara0.png", "capybara/capybara1.png",
        "capybara/capybara2.png", "capybara/capybara3.png",
        "capybara/capybara4.png", "capybara/capybara5.png",
        "capybara/capybara6.png"
    };
    
    for (const auto& file : frameFiles) {
        auto texture = std::make_unique<sf::Texture>();
        if (!texture->loadFromFile(file)) {
            std::cerr << "Error loading player texture: " << file << std::endl;
            return -1;
        }
        textures.push_back(std::move(texture));
    }
    
    // Load the background
    auto backgroundTexture = std::make_unique<sf::Texture>();
    sf::Sprite background;
    
    if (!backgroundTexture->loadFromFile("back.PNG")) {
        std::cerr << "Error loading background texture!" << std::endl;
        return -1;
    }
    
    // Set the background texture to the sprite
    background.setTexture(*backgroundTexture);
    gameObjectsDrawable.push_back(&background); // Store background in drawable list
    
    // Create a player sprite and set its texture
    sf::Sprite player;
    player.setTexture(*textures[0]);
    player.setPosition(400.0f, 810.0f);  // Start player at a position
    player.setScale(2.0f, 2.0f); // Scale capybara sprite
    gameObjectsDrawable.push_back(&player); // Store player in drawable list
    
    // Load the texture for the carrot
    auto carrotTexture = std::make_unique<sf::Texture>();
    if (!carrotTexture->loadFromFile("carrot.png")) {
        std::cerr << "Error loading carrot texture!" << std::endl;
        return -1;
    }
    textures.push_back(std::move(carrotTexture));
    
    // Create a carrot sprite and set its texture
    sf::Sprite carrot;
    carrot.setTexture(*textures.back());
    carrot.setPosition(400.0f, 0.0f);
    carrot.setScale(2.0f, 2.0f); // Scale carrot sprite
    gameObjectsDrawable.push_back(&carrot); // Store carrot in drawable list
    
    // Define movement speed for both objects
    float playerSpeed = 500.0f; // Adjusted for reasonable movement speed
    float carrotSpeed = 150.0f;
    
    // SFML clock for delta time
    sf::Clock clock;
    
    // Animation variables
    int currentFrame = 0;
    float frameDuration = 0.1f; // Time between frames
    float elapsedTime = 0.0f; // Track elapsed time for animation
    
    // State variables for catching the carrot
    bool caughtCarrot = false;
    sf::Clock catchClock; // Timer for the catch effect
    const float catchDuration = 1.0f; // Duration of the catch effect
    const sf::Color catchColor = sf::Color::Yellow; // Color to indicate catch
    
    // Direction variable for the player
    int playerDirection = 1; // 1 for right, -1 for left
    
    while (window.isOpen()) {
        // Handle events
        sf::Event event;
        sf::Time deltaTime = clock.restart();
        float dt = deltaTime.asSeconds();
        elapsedTime += dt; // Increment elapsed time
        
        while (window.pollEvent(event)) {
            if (event.type == sf::Event::Closed)
                window.close();
            
            // Player movement with arrow keys
            sf::Vector2f position = player.getPosition();
            if (event.type == sf::Event::KeyPressed) {
                if (event.key.code == sf::Keyboard::Left) {
                    playerDirection = -1; // Move left
                } else if (event.key.code == sf::Keyboard::Right) {
                    playerDirection = 1; // Move right
                }
            }
        }
        
        // Move the player based on the direction
        player.move(playerDirection * playerSpeed * dt, 0);
        
        // Check for boundaries and reverse direction on key press
        if (player.getPosition().x < 0 && playerDirection == -1) {
            player.setPosition(0, player.getPosition().y); // Prevent moving out of bounds
            playerDirection = 1; // Reverse direction to right
        } else if (player.getPosition().x > 1950 - player.getGlobalBounds().width && playerDirection == 1) {
            player.setPosition(1950 - player.getGlobalBounds().width, player.getPosition().y); // Prevent moving out of bounds
            playerDirection = -1; // Reverse direction to left
        }
        
        carrot.move(0, carrotSpeed * dt);
        
        // If the carrot goes off-screen, reset it to the top
        if (carrot.getPosition().y > 1118) {
            carrot.setPosition(rand() % (1950 - (int)carrot.getGlobalBounds().width), 0);
        }
        
        // Collision detection between player and carrot
        if (carrot.getGlobalBounds().intersects(player.getGlobalBounds())) {
            if (!caughtCarrot) {
                // Reset carrot position when the player "catches" it
                carrot.setPosition(rand() % (1950 - (int)carrot.getGlobalBounds().width), 0);
                caughtCarrot = true; // Mark as caught
                player.setColor(catchColor); // Change color to indicate catch
                catchClock.restart(); // Restart the timer
            }
        }
        
        // Reset the color after the catch duration
        if (caughtCarrot && catchClock.getElapsedTime().asSeconds() >= catchDuration) {
            caughtCarrot = false; // Reset caught state
            player.setColor(sf::Color::White); // Reset color
        }
        
        // Animation logic for the capybara
        if (!caughtCarrot && elapsedTime >= frameDuration) {
            elapsedTime -= frameDuration; // Reset elapsed time
            currentFrame = (currentFrame + 1) % (textures.size() - 1); // Cycle through capybara frames
            player.setTexture(*textures[currentFrame]); // Update the player's texture
        }
        
        // Clear the window
        window.clear();
        
        // Render all drawable objects
        renderObjects(gameObjectsDrawable, window);
        
        // Display everything on the screen
        window.display();
    }
    
    return 0;
}
