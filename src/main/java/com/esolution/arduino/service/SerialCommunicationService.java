package com.esolution.arduino.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fazecast.jSerialComm.SerialPort;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Service
public class SerialCommunicationService {
	private static final Logger logger = LoggerFactory.getLogger(SerialCommunicationService.class);
	
	private SerialPort serialPort;
	private static final String PORT_NAME = "COM7"; // Check connected port
	private static final int BAUD_RATE = 9600; 		// Check serial configuration

	private final Sinks.Many<String> messageSink = Sinks.many().replay().all(); 

    public Mono<Void> connect() {
    	return Mono.fromRunnable(() -> {
            serialPort = SerialPort.getCommPort(PORT_NAME);
            serialPort.setBaudRate(BAUD_RATE);
            if (!serialPort.openPort()) {
                throw new RuntimeException("Échec de la connexion au port série");
            }
            logger.info("Connecté au port série " + PORT_NAME);
        });
    }

    public Mono<Void> disconnect() {
        return Mono.fromRunnable(() -> {
            if (serialPort != null && serialPort.isOpen()) {
                serialPort.closePort();
                logger.info("Déconnecté du port série");
            }
        });
    }

    public Mono<Void> sendMessage(String message) {
        return Mono.fromRunnable(() -> {
            if (serialPort != null && serialPort.isOpen()) {
                byte[] messageBytes = message.getBytes();
                serialPort.writeBytes(messageBytes, messageBytes.length);
                logger.info("Message envoyé à l'Arduino : " + message);
            } else {
                throw new RuntimeException("Le port série n'est pas ouvert !");
            }
        });
    }

    public Mono<String> getMessages() {
        return messageSink.asFlux().next();
    }

}
