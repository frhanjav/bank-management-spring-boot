package com.example.bankmanagement.controller;

import com.example.bankmanagement.dto.AccountDto;
import com.example.bankmanagement.dto.CreateUserRequest;
import com.example.bankmanagement.dto.FixedDepositDto;
import com.example.bankmanagement.dto.LoanDto;
import com.example.bankmanagement.model.User;
import com.example.bankmanagement.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.bankmanagement.dto.GrievanceDto;
import com.example.bankmanagement.service.GrievanceService;

import java.util.List;

@Controller
@RequestMapping("/manager")
@PreAuthorize("hasRole('MANAGER')")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;
    private final GrievanceService grievanceService;


    @GetMapping("/dashboard")
    public String managerDashboard(Model model) {
        model.addAttribute("pendingAccountCount", managerService.getPendingAccounts().size());
        model.addAttribute("pendingLoanCount", managerService.getPendingLoans().size());
        model.addAttribute("pendingFdCount", managerService.getPendingFixedDeposits().size());
        return "dashboard-manager";
    }

    @GetMapping("/create-staff")
    public String showCreateStaffForm(Model model) {
        model.addAttribute("userRequest", new CreateUserRequest());
        return "manager/create-staff";
    }

    @PostMapping("/create-staff")
    public String createStaff(@ModelAttribute("userRequest") CreateUserRequest request, RedirectAttributes redirectAttributes) {
        try {
            managerService.createStaffUser(request);
            redirectAttributes.addFlashAttribute("successMessage", "Staff user created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create staff: " + e.getMessage());
        }
        return "redirect:/manager/dashboard";
    }

    @GetMapping("/pending-accounts")
    public String viewPendingAccounts(Model model) {
        List<AccountDto> pendingAccounts = managerService.getPendingAccounts();
        model.addAttribute("accounts", pendingAccounts);
        return "manager/pending-accounts";
    }

    @PostMapping("/accounts/{accountId}/approve")
    public String approveAccount(@PathVariable Long accountId, @AuthenticationPrincipal User currentUser, RedirectAttributes redirectAttributes) {
        try {
            managerService.approveAccount(accountId, currentUser.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Account approved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to approve account: " + e.getMessage());
        }
        return "redirect:/manager/pending-accounts";
    }

    @PostMapping("/accounts/{accountId}/reject")
    public String rejectAccount(@PathVariable Long accountId, @AuthenticationPrincipal User currentUser, RedirectAttributes redirectAttributes) {
        try {
            managerService.rejectAccount(accountId, currentUser.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Account rejected successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to reject account: " + e.getMessage());
        }
        return "redirect:/manager/pending-accounts";
    }


    @GetMapping("/pending-loans")
    public String viewPendingLoans(Model model) {
        List<LoanDto> pendingLoans = managerService.getPendingLoans();
        model.addAttribute("loans", pendingLoans);
        return "manager/pending-loans";
    }

    @PostMapping("/loans/{loanId}/approve")
    public String approveLoan(@PathVariable Long loanId, @AuthenticationPrincipal User currentUser, RedirectAttributes redirectAttributes) {
        try {
            managerService.approveLoan(loanId, currentUser.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Loan approved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to approve loan: " + e.getMessage());
        }
        return "redirect:/manager/pending-loans";
    }

    @PostMapping("/loans/{loanId}/reject")
    public String rejectLoan(@PathVariable Long loanId, @AuthenticationPrincipal User currentUser, RedirectAttributes redirectAttributes) {
        try {
            managerService.rejectLoan(loanId, currentUser.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Loan rejected successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to reject loan: " + e.getMessage());
        }
        return "redirect:/manager/pending-loans";
    }


    @GetMapping("/pending-fds")
    public String viewPendingFDs(Model model) {
        List<FixedDepositDto> pendingFDs = managerService.getPendingFixedDeposits();
        model.addAttribute("fds", pendingFDs);
        return "manager/pending-fds";
    }

    @PostMapping("/fds/{fdId}/approve")
    public String approveFd(@PathVariable Long fdId, @AuthenticationPrincipal User currentUser, RedirectAttributes redirectAttributes) {
        try {
            managerService.approveFixedDeposit(fdId, currentUser.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Fixed Deposit approved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to approve FD: " + e.getMessage());
        }
        return "redirect:/manager/pending-fds";
    }

    @PostMapping("/fds/{fdId}/reject")
    public String rejectFd(@PathVariable Long fdId, @AuthenticationPrincipal User currentUser, RedirectAttributes redirectAttributes) {
        try {
            managerService.rejectFixedDeposit(fdId, currentUser.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Fixed Deposit rejected successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to reject FD: " + e.getMessage());
        }
        return "redirect:/manager/pending-fds";
    }

    @GetMapping("/grievances")
    public String viewAllGrievances(Model model) {
        List<GrievanceDto> allGrievances = grievanceService.getAllGrievances();
        model.addAttribute("grievances", allGrievances);
        return "manager/view-grievances";
    }

}