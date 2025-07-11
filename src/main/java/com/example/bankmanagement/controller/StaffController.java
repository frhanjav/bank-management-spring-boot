package com.example.bankmanagement.controller;

import com.example.bankmanagement.dto.CreateUserRequest; // Import
import com.example.bankmanagement.model.AccountType; // Import
import com.example.bankmanagement.service.StaffService; // Inject StaffService
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.bankmanagement.dto.AccountDto;
import com.example.bankmanagement.dto.CustomerDto;
import com.example.bankmanagement.dto.GrievanceDto;
import com.example.bankmanagement.dto.TransactionRequestDto;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.service.*;

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
    private final StaffService staffService;

    @GetMapping("/dashboard")
    public String staffDashboard(Model model) {
        // Add data for staff dashboard, e.g., count of pending grievances
        model.addAttribute("pendingGrievanceCount", grievanceService.getPendingGrievances().size());
        return "dashboard-staff"; // dashboard-staff.html
    }

    // --- Add New Endpoint for Combined Creation ---
    @GetMapping("/create-customer-account")
    public String showCreateCustomerAccountForm(Model model) {
        model.addAttribute("customerRequest", new CreateUserRequest()); // DTO for form binding
        model.addAttribute("accountTypes", AccountType.values()); // For account type selection
        return "staff/create-customer-account"; // Path to the new template
    }

    @PostMapping("/create-customer-account")
    public String createCustomerAndAccount(@ModelAttribute("customerRequest") CreateUserRequest customerRequest,
                                           @RequestParam("accountType") AccountType accountType, // Get account type from form
                                           @AuthenticationPrincipal User currentUser,
                                           RedirectAttributes redirectAttributes) {
        try {
            staffService.createCustomerAndOpenAccount(customerRequest, accountType, currentUser.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Customer profile and account created. Pending Manager approval.");
        } catch (Exception e) {
            // Log the exception in a real app: log.error("...", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create customer/account: " + e.getMessage());
            // Optionally return back to the form with the error and retain input
            // model.addAttribute("customerRequest", customerRequest); // Need Model parameter then
            // model.addAttribute("accountTypes", AccountType.values());
            // return "staff/create-customer-account";
            return "redirect:/staff/create-customer-account"; // Redirect back for simplicity now
        }
        return "redirect:/staff/dashboard"; // Redirect to dashboard on success
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
