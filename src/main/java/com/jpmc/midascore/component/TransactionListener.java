package com.jpmc.midascore.component;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;

@Component
public class TransactionListener {

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final RestTemplate restTemplate;

    public TransactionListener(UserRepository userRepository,
            TransactionRecordRepository transactionRecordRepository,
            RestTemplate restTemplate) {

        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
        this.restTemplate = restTemplate;
    }

    @KafkaListener(topics = "transactions", groupId = "midas-core")
    public void listen(Transaction transaction) {

        UserRecord sender
                = userRepository.findById(transaction.getSenderId()).orElse(null);

        UserRecord recipient
                = userRepository.findById(transaction.getRecipientId()).orElse(null);

        if (sender == null || recipient == null) {
            return;
        }

        float amount = transaction.getAmount();

        // ✅ CALL INCENTIVE API
        String url = "http://localhost:8080/incentive";

        Incentive incentive
                = restTemplate.postForObject(url, transaction, Incentive.class);

        float incentiveAmount = 0f;

        if (incentive != null) {
            incentiveAmount = incentive.getAmount().floatValue();
        }

        // Check sender balance
        if (sender.getBalance() < amount) {
            return;
        }

        // Update balances
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount + incentiveAmount);

        userRepository.save(sender);
        userRepository.save(recipient);

        // Save transaction
        TransactionRecord record
                = new TransactionRecord(sender, recipient, amount);

        transactionRecordRepository.save(record);

        if (sender.getName().equals("wilbur")) {
    System.out.println("WILBUR BALANCE = " + sender.getBalance());
}

if (recipient.getName().equals("wilbur")) {
    System.out.println("WILBUR BALANCE = " + recipient.getBalance());
}

    }
}
