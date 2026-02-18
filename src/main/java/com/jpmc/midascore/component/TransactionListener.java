package com.jpmc.midascore.component;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;

@Component
public class TransactionListener {

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;

    public TransactionListener(UserRepository userRepository,
                               TransactionRecordRepository transactionRecordRepository) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
    }

    @KafkaListener(topics = "transactions", groupId = "midas-core")
    public void listen(Transaction transaction) {

        UserRecord sender =
                userRepository.findById(transaction.getSenderId()).orElse(null);

        UserRecord recipient =
                userRepository.findById(transaction.getRecipientId()).orElse(null);

        if (sender == null || recipient == null) {
            return;
        }

        float amount = transaction.getAmount();

        // Check balance
        if (sender.getBalance() < amount) {
            return;
        }

        // Update balances
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount);

        userRepository.save(sender);
        userRepository.save(recipient);

        // Save transaction
        TransactionRecord record =
        new TransactionRecord(sender, recipient, amount);

transactionRecordRepository.save(record);
if (sender.getName().equals("waldorf")) {
    System.out.println("WALDORF BALANCE = " + sender.getBalance());
}

if (recipient.getName().equals("waldorf")) {
    System.out.println("WALDORF BALANCE = " + recipient.getBalance());
}


    }
}
