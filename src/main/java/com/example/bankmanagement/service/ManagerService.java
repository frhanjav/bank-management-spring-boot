package com.example.bankmanagement.service;

import com.example.bankmanagement.dto.*;
import com.example.bankmanagement.model.Manager;
import com.example.bankmanagement.model.User;
import java.util.List;

public interface ManagerService {
    Manager findManagerByUser(User user);
    UserDto createStaffUser(CreateUserRequest request);

    // Approval workflows
    List<AccountDto> getPendingAccounts();
    AccountDto approveAccount(Long accountId, Long managerUserId);
    AccountDto rejectAccount(Long accountId, Long managerUserId);

    List<LoanDto> getPendingLoans();
    LoanDto approveLoan(Long loanId, Long managerUserId);
    LoanDto rejectLoan(Long loanId, Long managerUserId);

    List<FixedDepositDto> getPendingFixedDeposits();
    FixedDepositDto approveFixedDeposit(Long fdId, Long managerUserId);
    FixedDepositDto rejectFixedDeposit(Long fdId, Long managerUserId);
}