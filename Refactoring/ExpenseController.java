package com.adobe.prj.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.adobe.prj.entity.Employee;
import com.adobe.prj.entity.EmployeeRoles;
import com.adobe.prj.entity.Expense;
import com.adobe.prj.entity.ExpenseStatus;
import com.adobe.prj.entity.Project;
import com.adobe.prj.exceptions.BadRequestException;
import com.adobe.prj.exceptions.NotFoundException;
import com.adobe.prj.service.ExpenseService;
import com.adobe.prj.service.ProjectService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@CrossOrigin
@RestController
@RequestMapping("api/expenses")
@Api(
    value = "ExpenseController",
    description = "REST APIs related to Expense Entity"
)
/*Refactoring generale
	Migliorata l'indentazione delle funzioni per evitare eccessiva Horizontal Length
	Indentate le parentesi con maggiore efficacia per una migliore lettura
	Nella classe ExpenseService sono state aggiunte le classi isOwner(emp,expense) e
  isManager(emp,expense), per evitare di ripetere il controllo emp.getId() ==
  expense.getEmployee().getId()) e expense.getProject().getProjectManager()
  .getId() == emp.getId()) che precedentemente erano stati usati eccessivamente
  in questa classe
*/
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private ProjectService projectService;

    @ApiOperation(
        value = "get total expenses for a project",
        response = Map.class,
        tags = "getExpenseTotal"
    )
    @GetMapping("/projectAmount/{pid}")
    public @ResponseBody Map<String, Double> getExpenseTotal(
            @PathVariable("pid") int pid) {

        return expenseService.getExpenseTotal(pid);
    }

    private Employee getEmployee() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				Employee employee = (Employee) auth.getPrincipal();
				return employee;
    }

    @ApiOperation(
        value = "get an expense by ID",
        response = Expense.class,
        tags = "getExpense"
    )
    @GetMapping("/{eid}")
		//Refactoring:
		//expense invece di e
    public @ResponseBody Expense getExpense(
            @PathVariable("eid") int id)
            throws AccessDeniedException, NotFoundException {

        Expense expense = expenseService.getExpense(id);
        Employee emp = getEmployee();
				if (expenseService.isOwner(emp, expense) ||
            expenseService.isManager(emp, expense))
            || emp.getRole() == EmployeeRoles.ADMIN)
					return expense;
				throw new AccessDeniedException("You don't have access to this expense");
    }

    @ApiOperation(
        value = "add an expense",
        response = Expense.class,
        tags = "addExpense"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
		//Refactoring:
		//expense invece di e
		//project invece di p, e p invece di Project all'interno del for
    public @ResponseBody Expense addExpense(
            @RequestBody @Valid Expense expense)
            throws AccessDeniedException, NotFoundException {

        Employee emp = getEmployee();

        expense.setEmployee(employee);
        expense.setStatus(ExpenseStatus.SUBMITTED);
        Project project = projectService.getProject(
                        expense.getProject().getId());

        if (emp.getId() == project.getProjectManager().getId()) {
            return expenseService.addExpense(expense);
        }
        for (Project p : emp.getProjects()) {
            if (expense.getProject().getId() == p.getId()) {
                return expenseService.addExpense(expense);
            }
        }

				throw new AccessDeniedException("You are not part of the project");
    }

    @ApiOperation(
        value = "get expenses by a project",
        response = Iterable.class,
        tags = "getExpenseByProject"
    )
    @GetMapping("/projects/{pid}")
		//Refactoring:
		//expense invece di e
		//p invece di Project all'interno del for
    public @ResponseBody List<Expense> getExpenseByProject(
            @PathVariable("pid") int projectId)
            throws NotFoundException, AccessDeniedException {

        Employee emp = getEmployee();
        for (Project p : emp.getProjects()) {
					if (id == p.getId() && emp.getRole() == EmployeeRoles.PRJ_MANAGER)
						return expenseService.getExpenseByProject(id);
        }

        throw new AccessDeniedException("You are not part of the project");
    }

    @ApiOperation(
        value = "get an employee expenses",
        response = Iterable.class,
        tags = "getMyExpense"
    )
    @GetMapping
    public @ResponseBody List<Expense> getMyExpense(
            @RequestParam(name = "page", defaultValue = "-1") int page,
            @RequestParam(name = "size", defaultValue = "-1") int size)
            throws NotFoundException {

        Employee emp = getEmployee();
        return expenseService.getExpenseByEmplyoeeId(emp.getId());
    }

    @ApiOperation(
        value = "get expense approvals",
        response = Iterable.class,
        tags = "getApprovalsByStatus"
    )
    @GetMapping("/approvals/{status}")
    public @ResponseBody List<Expense> getApprovalsByStatus(
            @RequestParam(name = "page", defaultValue = "-1") int page,
            @RequestParam(name = "size", defaultValue = "-1") int size,
            @PathVariable("status") int status)
            throws NotFoundException {

        Employee emp = getEmployee();

        if (emp.getRole() == EmployeeRoles.PRJ_MANAGER) {
            List<Expense> expenses = new ArrayList<>();
            List<Project> all_projects = projectService.getAllProjects();
            for (Project p : all_projects) {
                if (p.getProjectManager().getId() == emp.getId()) {
                    expenses.addAll(
                            expenseService.getExpenseByStatusforProject(
                                    p.getId(),
                                    status));
                }
            }
            return expenses;
        }else if (emp.getRole() == EmployeeRoles.ADMIN) {
            return expenseService.getExpenseByStatus(status);
        }

        return new ArrayList<Expense>();
    }

    @ApiOperation(
        value = "update expense approvals",
        response = Expense.class,
        tags = "updateExpenseStatus"
    )
    @RequestMapping(
        value = "/updateStatus/{id}",
        method = RequestMethod.PATCH,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
		//Refactoring:
		//expense invece di e
		//Diviso l'if in più variabili per rendere più chiaro lo scopo
    public @ResponseBody Expense updateExpenseStatus(
            @RequestBody Map<String, Object> updates,
            @PathVariable("id") int id)
            throws NotFoundException, AccessDeniedException {

        Expense expense = expenseService.getExpense(id);
        Employee emp = getEmployee();

        boolean isAuthorized = emp.getRole() == EmployeeRoles.PRJ_MANAGER
                && expenseService.isManager(emp, expense);
                && expenseService.isOwner(emp, expense);

        boolean isAdmin = emp.getRole() == EmployeeRoles.ADMIN;

        boolean isSubmitted = expense.getStatus() == ExpenseStatus.SUBMITTED;

        if ((isAuthorized || isAdmin) && isSubmitted) {
            return expenseService.updateExpenseStatus(updates, expense);
        }

        throw new AccessDeniedException("Employee doesn't have access to update it");
    }

    @ApiOperation(
        value = "update expense",
        response = Expense.class,
        tags = "updateExpense"
    )
    @RequestMapping(
        value = "/{id}",
        method = RequestMethod.PATCH,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
		//Refactoring:
    //expense invece di e
		//Rimosso Commented-out code
		//Rimosso Noise-comment
		//Diviso l'if in più variabili per rendere più chiaro lo scopo
    public @ResponseBody Expense updateExpense(
            @RequestBody Map<String, Object> updates,
            @PathVariable("id") int id)
            throws NotFoundException,
                   AccessDeniedException,
                   BadRequestException {

        Expense expense = expenseService.getExpense(id);
        Employee emp = getEmployee();

        if (updates.containsKey("status") || updates.containsKey("employee")
                || updates.containsKey("project")) {
            throw new AccessDeniedException("Non Modifiable fields given");
        }

        boolean isSubmitted = expense.getStatus() == ExpenseStatus.SUBMITTED;

        if (expenseService.isOwner(emp,expense) && isSubmitted) {
            return expenseService.updateExpense(updates, expense);
        }

        throw new AccessDeniedException("Employee doesn't have access to update it");
    }

    @ApiOperation(
        value = "delete an expense",
        tags = "deleteExpense"
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
		//Refactoring:
		//expense invece di e
		//Diviso l'if in più variabili per rendere più chiaro lo scopo
    public void deleteExpense(
            @PathVariable("id") int id)
            throws NotFoundException, AccessDeniedException {

        Employee emp = getEmployee();
        Expense expense = expenseService.getExpense(id);
        boolean isSubmitted =  expense.getStatus() == ExpenseStatus.SUBMITTED;

        if (expenseService.isOwner(emp,expense) && isSubmitted) {
            expenseService.deleteExpense(id);
            return;
        }

        throw new AccessDeniedException("Employee doesn't have access to delete the expense");
    }

    @ApiOperation(
        value = "delete expenses",
        tags = "deleteExpenses"
    )
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
		//Refactoring:
		//expense invece di e
		//id_list invece di ids
		//Diviso l'if in più variabili per rendere più chiaro lo scopo
    public void deleteExpenses(
            @Valid @RequestBody List<Integer> id_list)
            throws NotFoundException, AccessDeniedException {

        Employee emp = getEmployee();
        for (int id : id_list) {
            Expense expense = expenseService.getExpense(id);
            boolean isSubmitted = expense.getStatus() == ExpenseStatus.SUBMITTED;

            if (!expenseService.isOwner(emp,expense) && isSubmitted) {
                throw new AccessDeniedException(
                        "Employee doesn't have access to delete one of the expenses");
            }
        }

        expenseService.deleteExpenses(ids);
    }
}
