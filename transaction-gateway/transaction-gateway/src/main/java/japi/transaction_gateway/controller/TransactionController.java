package japi.transaction_gateway.controller;

import java.util.UUID;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import japi.transaction_gateway.dto.TransactionEvent;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {
    

    //StreamBridge to send messages by demand
    private final StreamBridge streamBridge;

    @PostMapping
    public ResponseEntity<String> createTransacction(@RequestBody TransactionEvent event){
        //Generate id if dosent exist
        var eventWithId = new TransactionEvent(
            UUID.randomUUID(),
            event.userId(),
            event.amount(),
            event.currency(),
            event.type()
        );

        //send biding to YAML
        boolean sent = streamBridge.send("processTransaction-out-0", eventWithId);

        if( sent ){
            return ResponseEntity.accepted().body("Transaction sent to pipeline process");
        }else{
            return ResponseEntity.internalServerError().body("Error connecting to broker");
        }

    }

}
