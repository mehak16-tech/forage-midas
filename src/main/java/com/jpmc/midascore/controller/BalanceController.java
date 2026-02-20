package com.jpmc.midascore.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;

@RestController
public class BalanceController {

    private final UserRepository userRepository;

    public BalanceController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/balance")
public Balance getBalance(@RequestParam Long userId) {

    UserRecord user =
            userRepository.findById(userId).orElse(null);

    Balance balance = new Balance();

    if (user == null) {
        balance.setBalance(0);
    } else {
        balance.setBalance(user.getBalance());
    }

    return balance;
}


}
