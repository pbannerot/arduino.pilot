// Définir la broche de la LED
const int ledPin = 13;

void setup() {
  // Initialiser la broche de la LED comme une sortie
  pinMode(ledPin, OUTPUT);
  
  // Démarrer la communication série
  Serial.begin(9600);
}

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
