package com.esolution.arduino.service;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Service
public class SerialCommunicationService {
	private static final Logger logger = LoggerFactory.getLogger(SerialCommunicationService.class);
	
	private SerialPort serialPort;
	private static final String PORT_NAME = "COM5"; // Check connected port
	private static final int BAUD_RATE = 9600; 		// Check serial configuration
	private boolean listen = false;

	private final Sinks.Many<String> messageSink = Sinks.many().multicast().onBackpressureBuffer(); 

    public Mono<Void> connect() {
    	return Mono.fromRunnable(() -> {
            serialPort = SerialPort.getCommPort(PORT_NAME);
            serialPort.setBaudRate(BAUD_RATE);
            if (!serialPort.openPort()) {
                throw new RuntimeException("Échec de la connexion au port série");
            }
            logger.info("Connecté au port série " + PORT_NAME);
            startSerialListener();
        });
    }

    public Mono<Void> disconnect() {
        return Mono.fromRunnable(() -> {
            if (serialPort != null && serialPort.isOpen()) {
            	stopSerialListener();
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

    public Flux<String> getMessages() {
        return messageSink.asFlux();
    }
    
    private void startSerialListener() {
    	if (serialPort != null && serialPort.isOpen()) {
	    	if (listen == false) {
	    		listen = serialPort.addDataListener(new SerialPortDataListener() {
	
					@Override
					public int getListeningEvents() {
						return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
					}
	
					@Override
					public void serialEvent(SerialPortEvent event) {
						byte[] newData = event.getReceivedData();
		                String data = new String(newData, StandardCharsets.UTF_8);
		                messageSink.tryEmitNext(data);
		                logger.info("Message reçu : " + data);
					}
	    		});
	    	}
    	}
    }
    
    private void stopSerialListener() {
    	if (serialPort != null && serialPort.isOpen()) {
        	serialPort.removeDataListener();
        	listen = false;
    	}
    }

}
