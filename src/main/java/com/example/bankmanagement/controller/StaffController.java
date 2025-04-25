package com.example.bankmanagement.controller;

import com.example.bankmanagement.dto.AccountDto;
import com.example.bankmanagement.dto.CustomerDto;
import com.example.bankmanagement.dto.GrievanceDto;
import com.example.bankmanagement.dto.TransactionRequestDto;
import com.example.bankmanagement.model.AccountType;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/staff")
@PreAuthorize("hasRole('STAFF')")
@RequiredArgsConstructor
public class StaffController {

    private final AccountService accountService;
    private final CustomerService customerService;
    private final TransactionService transactionService;
    private final GrievanceService grievanceService; // Assuming staff can handle grievances

    @GetMapping("/dashboard")
    public String staffDashboard(Model model) {
        // Add data for staff dashboard, e.g., count of pending grievances
        model.addAttribute("pendingGrievanceCount", grievanceService.getPendingGrievances().size());
        return "dashboard-staff"; // dashboard-staff.html
    }

    // --- Account Opening ---
    @GetMapping("/customers")
    public String viewCustomersForAccountOpening(Model model) {
        // Show customers (created by manager) who might not have active accounts yet
        List<CustomerDto> customers = customerService.getAllCustomers();
        model.addAttribute("customers", customers);
        return "staff/view-customers"; // staff/view-customers.html
    }


    @GetMapping("/open-account/{customerId}")
    public String showOpenAccountForm(@PathVariable Long customerId, Model model) {
        CustomerDto customer = customerService.getCustomerDetails(customerService.findCustomerById(customerId).getUser().getUsername()); // Bit indirect, refactor if needed
        model.addAttribute("customer", customer);
        model.addAttribute("accountTypes", AccountType.values());
        model.addAttribute("accountRequest", new AccountDto()); // Use AccountDto or a specific request DTO
        return "staff/open-account"; // staff/open-account.html
    }

    @PostMapping("/open-account")
    public String openAccount(@RequestParam Long customerId,
                              @RequestParam AccountType accountType,
                              @AuthenticationPrincipal User currentUser,
                              RedirectAttributes redirectAttributes) {
        try {
            accountService.openAccount(customerId, accountType, currentUser.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Account opening initiated. Pending Manager approval.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to initiate account opening: " + e.getMessage());
        }
        return "redirect:/staff/customers"; // Redirect back to customer list or dashboard
    }

    // --- Transactions ---
    @GetMapping("/transactions")
    public String showTransactionForm(Model model) {
        model.addAttribute("transactionRequest", new TransactionRequestDto());
        return "staff/manage-transactions"; // staff/manage-transactions.html
    }

    @PostMapping("/transactions/deposit")
    public String performDeposit(@ModelAttribute TransactionRequestDto request, @AuthenticationPrincipal User currentUser, RedirectAttributes redirectAttributes) {
        try {
            transactionService.performDeposit(request, currentUser.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Deposit successful!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Deposit failed: " + e.getMessage());
        }
        return "redirect:/staff/transactions";
    }

    @PostMapping("/transactions/withdraw")
    public String performWithdrawal(@ModelAttribute TransactionRequestDto request, @AuthenticationPrincipal User currentUser, RedirectAttributes redirectAttributes) {
        try {
            transactionService.performWithdrawal(request, currentUser.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Withdrawal successful!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Withdrawal failed: " + e.getMessage());
        }
        return "redirect:/staff/transactions";
    }

    // --- Grievance Handling (Optional for Staff) ---
    @GetMapping("/grievances")
    public String viewPendingGrievances(Model model) {
        List<GrievanceDto> grievances = grievanceService.getPendingGrievances();
        model.addAttribute("grievances", grievances);
        return "staff/handle-grievances"; // staff/handle-grievances.html
    }

    @PostMapping("/grievances/{grievanceId}/resolve")
    public String resolveGrievance(@PathVariable Long grievanceId, @AuthenticationPrincipal User currentUser, RedirectAttributes redirectAttributes) {
        try {
            grievanceService.resolveGrievance(grievanceId, currentUser.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Grievance resolved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to resolve grievance: " + e.getMessage());
        }
        return "redirect:/staff/grievances";
    }

    // TODO: Add endpoints for viewing/handling customer profile update requests if needed
}
