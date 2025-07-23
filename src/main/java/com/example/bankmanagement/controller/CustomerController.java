package com.example.bankmanagement.controller;

import com.example.bankmanagement.dto.*;
import com.example.bankmanagement.exception.UnauthorizedOperationException;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.service.*;
import com.example.bankmanagement.service.GrievanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/customer")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final LoanService loanService;
    private final FixedDepositService fixedDepositService;
    private final GrievanceService grievanceService;


    @GetMapping("/dashboard")
    public String customerDashboard(Model model, @AuthenticationPrincipal User currentUser) {
        log.info("Loading customer dashboard for user: {}", currentUser.getUsername());
        try {
            CustomerDto customer = customerService.getCustomerDetails(currentUser.getUsername());
            model.addAttribute("customer", customer);

            if (customer != null) {
                Long customerId = customer.getId();

                List<AccountDto> accounts = accountService.getCustomerAccounts(customerId);
                List<LoanDto> loans = loanService.getCustomerLoans(customerId);
                List<FixedDepositDto> fixedDeposits = fixedDepositService.getCustomerFixedDeposits(customerId);
                // --- Fetch Grievances ---
                List<GrievanceDto> grievances = grievanceService.getCustomerGrievances(customerId);

                model.addAttribute("accounts", accounts);
                model.addAttribute("loans", loans);
                model.addAttribute("fixedDeposits", fixedDeposits);
                model.addAttribute("grievances", grievances);

                log.info("Found {} accounts, {} loans, {} FDs, {} grievances for customer ID: {}",
                        accounts != null ? accounts.size() : 0,
                        loans != null ? loans.size() : 0,
                        fixedDeposits != null ? fixedDeposits.size() : 0,
                        grievances != null ? grievances.size() : 0,
                        customerId);
            } else {
                log.warn("Customer profile not found for user: {}. Displaying limited dashboard.", currentUser.getUsername());
                model.addAttribute("accounts", Collections.emptyList());
                model.addAttribute("loans", Collections.emptyList());
                model.addAttribute("fixedDeposits", Collections.emptyList());
                model.addAttribute("grievances", Collections.emptyList());
            }

            return "dashboard-customer";

        } catch (Exception e) {
            log.error("Error loading customer dashboard for user: {}", currentUser.getUsername(), e);
            model.addAttribute("errorMessage", "Could not load dashboard details. Please try again later.");
            model.addAttribute("customer", null);
            model.addAttribute("accounts", Collections.emptyList());
            model.addAttribute("loans", Collections.emptyList());
            model.addAttribute("fixedDeposits", Collections.emptyList());
            model.addAttribute("grievances", Collections.emptyList());
            return "dashboard-customer";
        }
    }

    // --- Other methods remain the same ---
    @GetMapping("/account/{accountId}")
    public String viewAccountDetails(@PathVariable Long accountId, Model model, @AuthenticationPrincipal User currentUser) {
        log.info("Fetching details for account ID: {}", accountId);
        try {
            CustomerDto currentCustomer = customerService.getCustomerDetails(currentUser.getUsername());
            if (currentCustomer == null || !currentCustomer.isActive()) {
                log.warn("Inactive customer {} attempted to view account {}", currentUser.getUsername(), accountId);
                throw new UnauthorizedOperationException("Your account must be active to view account details.");
            }
            Long customerId = currentCustomer.getId();

            AccountDto account = accountService.getAccountById(accountId);
            log.info("Account found: {}", account);

            if (account.getCustomerId() == null || !customerId.equals(account.getCustomerId())) {
                log.warn("Unauthorized access attempt by customer {} for account {}", customerId, accountId);
                throw new UnauthorizedOperationException("Access denied to this account.");
            }

            List<TransactionDto> transactions = transactionService.getAccountTransactions(account.getAccountNumber());
            log.info("Transactions found: {} transactions", transactions != null ? transactions.size() : "null list");

            model.addAttribute("account", account);
            model.addAttribute("transactions", transactions);
            log.info("Rendering view customer/view-account for account ID: {}", accountId);
            return "customer/view-account";

        } catch (Exception e) {
            log.error("Error fetching details for account ID: {}", accountId, e);
            throw e;
        }
    }

    // Loan Application
    @GetMapping("/apply-loan")
    public String showApplyLoanForm(Model model, @AuthenticationPrincipal User currentUser) {
        CustomerDto currentCustomer = customerService.getCustomerDetails(currentUser.getUsername());
        if (currentCustomer == null || !currentCustomer.isActive()) {
            throw new UnauthorizedOperationException("Your account must be active to apply for a loan.");
        }
        model.addAttribute("loanRequest", new LoanDto());
        return "customer/apply-loan";
    }

    @PostMapping("/apply-loan")
    public String applyForLoan(@RequestParam BigDecimal loanAmount,
                               @RequestParam Double interestRate,
                               @RequestParam Integer termMonths,
                               @AuthenticationPrincipal User currentUser,
                               RedirectAttributes redirectAttributes) {
        try {
            CustomerDto currentCustomer = customerService.getCustomerDetails(currentUser.getUsername());
            if (currentCustomer == null || !currentCustomer.isActive()) {
                throw new UnauthorizedOperationException("Your account must be active to apply for a loan.");
            }
            Long customerId = currentCustomer.getId();
            loanService.applyForLoan(customerId, loanAmount, interestRate, termMonths);
            redirectAttributes.addFlashAttribute("successMessage", "Loan application submitted successfully. Pending Manager approval.");
        } catch (Exception e) {
            log.error("Loan application failed for user {}", currentUser.getUsername(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Loan application failed: " + e.getMessage());
        }
        return "redirect:/customer/dashboard";
    }

    // Fixed Deposit Application
    @GetMapping("/apply-fd")
    public String showApplyFdForm(Model model, @AuthenticationPrincipal User currentUser) {
        CustomerDto currentCustomer = customerService.getCustomerDetails(currentUser.getUsername());
        if (currentCustomer == null || !currentCustomer.isActive()) {
            throw new UnauthorizedOperationException("Your account must be active to apply for a Fixed Deposit.");
        }
        model.addAttribute("fdRequest", new FixedDepositDto());
        return "customer/apply-fd";
    }

    @PostMapping("/apply-fd")
    public String applyForFd(@RequestParam BigDecimal principalAmount,
                             @RequestParam Double interestRate,
                             @RequestParam Integer termMonths,
                             @AuthenticationPrincipal User currentUser,
                             RedirectAttributes redirectAttributes) {
        try {
            CustomerDto currentCustomer = customerService.getCustomerDetails(currentUser.getUsername());
            if (currentCustomer == null || !currentCustomer.isActive()) {
                throw new UnauthorizedOperationException("Your account must be active to apply for a Fixed Deposit.");
            }
            Long customerId = currentCustomer.getId();
            fixedDepositService.applyForFixedDeposit(customerId, principalAmount, interestRate, termMonths);
            redirectAttributes.addFlashAttribute("successMessage", "Fixed Deposit application submitted successfully. Pending Manager approval.");
        } catch (Exception e) {
            log.error("FD application failed for user {}", currentUser.getUsername(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Fixed Deposit application failed: " + e.getMessage());
        }
        return "redirect:/customer/dashboard";
    }

    // Grievance Filing
    @GetMapping("/file-grievance")
    public String showFileGrievanceForm(Model model, @AuthenticationPrincipal User currentUser) {
        model.addAttribute("grievanceRequest", new GrievanceDto());
        return "customer/file-grievance";
    }

    @PostMapping("/file-grievance")
    public String fileGrievance(@RequestParam String subject,
                                @RequestParam String description,
                                @AuthenticationPrincipal User currentUser,
                                RedirectAttributes redirectAttributes) {
        try {
            CustomerDto currentCustomer = customerService.getCustomerDetails(currentUser.getUsername());
            if (currentCustomer == null) {
                throw new UnauthorizedOperationException("Customer profile not found.");
            }
            Long customerId = currentCustomer.getId();
            grievanceService.fileGrievance(customerId, subject, description);
            redirectAttributes.addFlashAttribute("successMessage", "Grievance submitted successfully.");
        } catch (Exception e) {
            log.error("Grievance submission failed for user {}", currentUser.getUsername(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to submit grievance: " + e.getMessage());
        }
        return "redirect:/customer/dashboard";
    }

    // Profile Update Request
    @GetMapping("/profile/update")
    public String showUpdateProfileForm(Model model, @AuthenticationPrincipal User currentUser) {
        CustomerDto customer = customerService.getCustomerDetails(currentUser.getUsername());
        if (customer == null) {
            throw new UnauthorizedOperationException("Customer profile not found.");
        }
        model.addAttribute("customer", customer);
        return "customer/update-profile";
    }

    @PostMapping("/profile/update")
    public String requestProfileUpdate(@ModelAttribute CustomerDto updateRequest,
                                       @AuthenticationPrincipal User currentUser,
                                       RedirectAttributes redirectAttributes) {
        try {
            CustomerDto requestData = new CustomerDto();
            requestData.setPanNumber(updateRequest.getPanNumber());
            requestData.setAddress(updateRequest.getAddress());

            customerService.requestProfileUpdate(currentUser.getUsername(), requestData);
            redirectAttributes.addFlashAttribute("successMessage", "Profile update requested. Pending review.");
        } catch (Exception e) {
            log.error("Profile update request failed for user {}", currentUser.getUsername(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to request profile update: " + e.getMessage());
        }
        return "redirect:/customer/dashboard";
    }
}