#include <Arduino.h>  // Inclure la bibliothèque principale d'Arduino

// Définir la broche de la LED
const int ledPin = 13;

// La fonction setup() est utilisée pour l'initialisation
void setup() {
  // Initialiser la broche de la LED comme une sortie
  pinMode(ledPin, OUTPUT);
  
  // Démarrer la communication série à 9600 bauds
  Serial.begin(9600);
}

// La fonction loop() est utilisée pour exécuter le code en boucle
void loop() {
  // Vérifier s'il y a des données disponibles dans le port série
  if (Serial.available() > 0) {
    // Lire le message envoyé depuis le port série
    char receivedChar = Serial.read();
    
    // Si le caractère reçu est '1', allumer la LED
    if (receivedChar == '1') {
      digitalWrite(ledPin, HIGH);
      Serial.println("LED allumée");
    }
    // Si le caractère reçu est '0', éteindre la LED
    else if (receivedChar == '0') {
      digitalWrite(ledPin, LOW);
      Serial.println("LED éteinte");
    }
  }
}

// Implémentation du main() pour un environnement C++ classique
int main() {
    // Appel à l'initialisation d'Arduino (setup)
    init(); // Initialiser l'Arduino (nécessaire avant d'utiliser pinMode, Serial, etc.)
    
    // Appel à la fonction setup()
    setup();
    
    // Boucle infinie qui appelle la fonction loop() d'Arduino
    while (true) {
        loop();
    }
    
    return 0; // Bien que cette ligne ne soit jamais atteinte (en raison de la boucle infinie)
}
