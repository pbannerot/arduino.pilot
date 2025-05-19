package com.esolution.arduino.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.esolution.arduino.service.SerialCommunicationService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/arduino")
public class ArduinoController {
	@Autowired
	private SerialCommunicationService serialCommunicationService;

	// POST http://localhost:8080/arduino/connect
	@PostMapping("/connect")
	public Mono<String> connect() {
        return serialCommunicationService.connect()
                .then(Mono.just("Connecté au port série"))
                .onErrorResume(e -> Mono.just("Échec de la connexion au port série: " + e.getMessage()));
    }

	// POST http://localhost:8080/arduino/disconnect
	@PostMapping("/disconnect")
	public Mono<String> disconnect() {
        return serialCommunicationService.disconnect()
                .then(Mono.just("Déconnecté du port série"))
                .onErrorResume(e -> Mono.just("Erreur lors de la déconnexion: " + e.getMessage()));
    }


	// POST http://localhost:8080/arduino/send?message=1
	// POST http://localhost:8080/arduino/send?message=0
	@PostMapping("/send")
	public Mono<String> sendMessage(@RequestParam String message) {
        return serialCommunicationService.sendMessage(message)
                .then(Mono.just("Message envoyé à l'Arduino : " + message))
                .onErrorResume(e -> Mono.just("Erreur lors de l'envoi du message : " + e.getMessage()));
    }

	// GET http://localhost:8080/arduino/read
	@GetMapping(value = "/read", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> readMessage() {
        return serialCommunicationService.getMessages();
    }
}
