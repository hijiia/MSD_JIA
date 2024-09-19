#include <SFML/Graphics.hpp>
#include <vector>
#include <SFML/Window.hpp>
#include <iostream>
#include <memory>

//doing the drawing on the screen
void renderObjects(const std::vector<sf::Drawable*> & objects,  sf::RenderWindow & window){

    for (auto object: objects)
        window.draw(*object);
    
}


int main() {
    
    // Create the window
    sf::RenderWindow window(sf::VideoMode(800, 600), "Capybara Love Carrots");
    
    //hold the drawable in objects to use them in the draw function.
    std::vector<sf::Drawable*> gameObjectsDrawable;
    
    //holding the textures for the sprites
    std::vector<std::unique_ptr<sf::Texture>> textures;
    
    // Load the background
        std::unique_ptr<sf::Texture> backgroundTexture;
        sf::Sprite background;

//        if (!loadBackground(background, backgroundTexture, "park_background.png")) {
//            return -1; // Exit if background loading fails
//        }
    
    // Load the texture frames
    std::vector<std::string> frameFiles = {"capybara/capybara0.png", "capybara/capybara1.png", "capybara/capybara2.png", "capybara/capybara3.png", "capybara/capybara4.png", "capybara/capybara5.png", "capybara/capybara6.png"};
    for (const auto& file : frameFiles){
        auto texture = std::make_unique <sf::Texture>();
        if (!texture->loadFromFile(file)){
            std::cerr << "Error loading player texture!" << file << std::endl;
            return -1;
        }
        textures.push_back(std::move(texture));
    }
    
    
    // Create a player sprite and set its texture
    sf::Sprite player;
    //player.setRotation(90);
    player.setTexture(*textures[0]);
    player.setPosition(400.0f, 500.0f);  // Start player at a position
    
    //    // Load the texture for the falling box
    std::unique_ptr<sf::Texture> carrotTexture = std::make_unique<sf::Texture>();
    if(!(*carrotTexture).loadFromFile("carrot.png")) {
        std::cerr << "Error loading carrot texture!" << std::endl;
        return -1;
    }
    textures.push_back(std::move(carrotTexture));
    
    //    // Create a box sprite and set its texture
    sf::Sprite carrot;
    (carrot).setTexture(*textures.back());;
    (carrot).setPosition(400.0f, 0.0f);
    
    
    //    // Define movement speed for both objects
    float playerSpeed = 35000.f;
    float carrotSpeed = 150.0f;
    //
    // SFML clock for delta time
    sf::Clock clock;
    
    gameObjectsDrawable.push_back(&player);
    gameObjectsDrawable.push_back(&carrot);
    
    while (window.isOpen()) {
    // Handle events
    sf::Event event;
    sf::Time deltaTime = clock.restart();
    float dt = deltaTime.asSeconds();
        
    while (window.pollEvent(event)) {
        if (event.type == sf::Event::Closed)
        window.close();

    // Player movement with arrow keys
    sf::Vector2 position = player.getPosition();
        if (event.key.code == sf:: Keyboard::Left && event.type == sf::Event::KeyPressed){
                
        if (position.x > 0){
            player.move(-playerSpeed * dt, 0);
                }
            }
            
        if (event.key.code == sf:: Keyboard::Right && event.type == sf::Event::KeyPressed){
                
        if (position.x < 760){
            player.move(+playerSpeed * dt, 0);
                }
            }
        }
    
        
        carrot.move(0, carrotSpeed * dt);

        
        // If the box goes off-screen, reset it to the top
        if (carrot.getPosition().y > 600) {
            carrot.setPosition(rand() % (800 - (int)carrot.getGlobalBounds().width), 0);
        }
        
        // Collision detection between player and box
        if (carrot.getGlobalBounds().intersects(player.getGlobalBounds())) {
            // Reset box position when the player "catches" the box
            carrot.setPosition(rand() % (800 - (int)carrot.getGlobalBounds().width), 0);
        }
        
        // Clear the window
        window.clear();
        
        window.draw(background);

        renderObjects(gameObjectsDrawable, window);
        // Display everything on the screen
        window.display();
    }
    
    return 0;
}
