package com.example.bankmanagement.service.impl;

import com.example.bankmanagement.dto.*;
import com.example.bankmanagement.exception.InvalidRequestException;
import com.example.bankmanagement.exception.ResourceNotFoundException;
import com.example.bankmanagement.exception.UnauthorizedOperationException;
import com.example.bankmanagement.mapper.*;
import com.example.bankmanagement.model.*;
import com.example.bankmanagement.repository.*;
import com.example.bankmanagement.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManagerServiceImpl implements ManagerService {

    private final UserRepository userRepository;
    private final ManagerRepository managerRepository;
    private final StaffRepository staffRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final LoanRepository loanRepository;
    private final FixedDepositRepository fixedDepositRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AccountMapper accountMapper;
    private final LoanMapper loanMapper;
    private final FixedDepositMapper fdMapper;
    private final UserService userService; // Use UserService for user creation logic


    @Override
    public Manager findManagerByUser(User user) {
        return managerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Manager profile not found for user: " + user.getUsername()));
    }

    private Manager findManagerById(Long managerUserId) {
        User managerUser = userService.findUserById(managerUserId);
        if(managerUser.getRole() != Role.MANAGER) {
            throw new UnauthorizedOperationException("User is not a manager");
        }
        return findManagerByUser(managerUser);
    }


    @Override
    @Transactional
    public UserDto createStaffUser(CreateUserRequest request) {
        request.setRole(Role.STAFF);
        if (request.getEmployeeId() == null || request.getEmployeeId().isBlank()) {
            throw new InvalidRequestException("Employee ID is required for Staff");
        }
        // Create the base user
        UserDto userDto = userService.createUser(request);
        User user = userService.findUserById(userDto.getId());

        // Create the staff profile
        Staff staff = new Staff();
        staff.setName(request.getName());
        staff.setEmployeeId(request.getEmployeeId());
        staff.setUser(user);
        staffRepository.save(staff);

        return userDto; // Return the created user DTO
    }

    @Override
    @Transactional
    public UserDto createCustomerUser(CreateUserRequest request) {
        request.setRole(Role.CUSTOMER);
        if (request.getEmail() == null || request.getEmail().isBlank() ||
                request.getPhone() == null || request.getPhone().isBlank() ||
                request.getAddress() == null || request.getAddress().isBlank() ||
                request.getName() == null || request.getName().isBlank()) {
            throw new InvalidRequestException("Name, Email, Phone, and Address are required for Customer");
        }

        // Create the base user (will be disabled initially)
        UserDto userDto = userService.createUser(request);
        User user = userService.findUserById(userDto.getId());

        // Create the customer profile (inactive initially)
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setPanNumber(request.getPanNumber()); // Optional
        customer.setUser(user);
        customer.setActive(false); // Customer is inactive until first account approval
        customerRepository.save(customer);

        return userDto;
    }

    // --- Approval Methods ---

    @Override
    @Transactional(readOnly = true)
    public List<AccountDto> getPendingAccounts() {
        return accountRepository.findByStatus(AccountStatus.PENDING_APPROVAL).stream()
                .map(accountMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AccountDto approveAccount(Long accountId, Long managerUserId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + accountId));
        Manager approvingManager = findManagerById(managerUserId); // Verify manager exists

        if (account.getStatus() != AccountStatus.PENDING_APPROVAL) {
            throw new InvalidRequestException("Account is not pending approval.");
        }

        account.setStatus(AccountStatus.ACTIVE);
        account.setApprovedAt(LocalDateTime.now());
        account.setApprovedBy(approvingManager);

        // Activate the customer and enable their login if this is their first ACTIVE account
        Customer customer = account.getCustomer();
        boolean hasActiveAccount = customer.getAccounts().stream()
                .anyMatch(acc -> acc.getStatus() == AccountStatus.ACTIVE && !acc.getId().equals(accountId)); // Check other accounts

        if (!customer.isActive() && !hasActiveAccount) {
            customer.setActive(true);
            User customerUser = customer.getUser();
            if (customerUser != null) {
                customerUser.setEnabled(true); // Enable login
                userRepository.save(customerUser);
            }
            customerRepository.save(customer); // Save customer active status
            // TODO: Implement sending credentials (e.g., email/SMS - requires integration)
            System.out.println("Customer " + customer.getName() + " activated. Credentials should be sent.");
        }


        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDto(savedAccount);
    }

    @Override
    @Transactional
    public AccountDto rejectAccount(Long accountId, Long managerUserId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with ID: " + accountId));
        findManagerById(managerUserId); // Verify manager exists and has role

        if (account.getStatus() != AccountStatus.PENDING_APPROVAL) {
            throw new InvalidRequestException("Account is not pending approval.");
        }

        account.setStatus(AccountStatus.REJECTED);
        // Optionally set approvedBy to the rejecting manager for tracking
        // account.setApprovedBy(approvingManager);
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDto(savedAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanDto> getPendingLoans() {
        return loanRepository.findByStatus(RequestStatus.PENDING).stream()
                .map(loanMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LoanDto approveLoan(Long loanId, Long managerUserId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with ID: " + loanId));
        Manager approvingManager = findManagerById(managerUserId);

        if (loan.getStatus() != RequestStatus.PENDING) {
            throw new InvalidRequestException("Loan is not pending approval.");
        }
        loan.setStatus(RequestStatus.APPROVED); // Or ACTIVE if disbursed immediately
        loan.setApprovedAt(LocalDateTime.now());
        loan.setApprovedBy(approvingManager);
        Loan savedLoan = loanRepository.save(loan);
        return loanMapper.toDto(savedLoan);
    }

    @Override
    @Transactional
    public LoanDto rejectLoan(Long loanId, Long managerUserId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found with ID: " + loanId));
        findManagerById(managerUserId); // Verify manager

        if (loan.getStatus() != RequestStatus.PENDING) {
            throw new InvalidRequestException("Loan is not pending approval.");
        }
        loan.setStatus(RequestStatus.REJECTED);
        // loan.setApprovedBy(manager); // Optional: track rejector
        Loan savedLoan = loanRepository.save(loan);
        return loanMapper.toDto(savedLoan);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FixedDepositDto> getPendingFixedDeposits() {
        return fixedDepositRepository.findByStatus(RequestStatus.PENDING).stream()
                .map(fdMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FixedDepositDto approveFixedDeposit(Long fdId, Long managerUserId) {
        FixedDeposit fd = fixedDepositRepository.findById(fdId)
                .orElseThrow(() -> new ResourceNotFoundException("Fixed Deposit not found with ID: " + fdId));
        Manager approvingManager = findManagerById(managerUserId);

        if (fd.getStatus() != RequestStatus.PENDING) {
            throw new InvalidRequestException("FD is not pending approval.");
        }
        fd.setStatus(RequestStatus.ACTIVE);
        fd.setApprovedAt(LocalDateTime.now());
        fd.setApprovedBy(approvingManager);
        // Calculate and set maturity date
        fd.setMaturityDate(LocalDate.now().plusMonths(fd.getTermMonths()));
        FixedDeposit savedFd = fixedDepositRepository.save(fd);
        return fdMapper.toDto(savedFd);
    }

    @Override
    @Transactional
    public FixedDepositDto rejectFixedDeposit(Long fdId, Long managerUserId) {
        FixedDeposit fd = fixedDepositRepository.findById(fdId)
                .orElseThrow(() -> new ResourceNotFoundException("Fixed Deposit not found with ID: " + fdId));
        findManagerById(managerUserId); // Verify manager

        if (fd.getStatus() != RequestStatus.PENDING) {
            throw new InvalidRequestException("FD is not pending approval.");
        }
        fd.setStatus(RequestStatus.REJECTED);
        FixedDeposit savedFd = fixedDepositRepository.save(fd);
        return fdMapper.toDto(savedFd);
    }
}