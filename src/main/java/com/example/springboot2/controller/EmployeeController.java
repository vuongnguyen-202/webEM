package com.example.springboot2.controller;

import com.example.springboot2.model.Company;
import com.example.springboot2.model.Employee;
import com.example.springboot2.repository.CompanyRepository;
import com.example.springboot2.repository.EmployeeRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class EmployeeController {
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    CompanyRepository companyRepository;

    @RequestMapping(value = "/employee/list") // homepage redirects here
    public String getAllEmployee(
            @RequestParam(value = "company", required = false, defaultValue = "0") Long comId,
            @RequestParam(value = "sort", required = false, defaultValue = "0") int sortMode,
            Model model) {
        model.addAttribute("comId", comId);
        model.addAttribute("sortMode", sortMode);

        Sort.Direction sortOrder = Sort.Direction.DESC; // default sort order
        String sortColumn = "id"; // default sort column
        if (sortMode == 1 || sortMode == 2) {
            sortOrder = Sort.Direction.ASC;
        }
        if (sortMode == 2 || sortMode == 3) {
            sortColumn = "name";
        }

        List<Employee> employees = null;
        if (comId != 0) {
            Optional<Company> comp = companyRepository.findById(comId);
            if (comp.isPresent()) {
                employees = employeeRepository.findByCompany(
                        comp.get(),
                        Sort.by(sortOrder, sortColumn)
                );
            }
        }
        // employees may still be null
        if (employees == null) {
            employees = employeeRepository.findAll(Sort.by(sortOrder, sortColumn));
        }
        model.addAttribute("employees", employees);
        model.addAttribute("companies", companyRepository.findAll());
        return "employeeList";
    }

    @RequestMapping(value = "/employee/{id}")
    public String getEmployeeById(@PathVariable(value = "id") Long id, Model model) {
        Employee employee = employeeRepository.getById(id);
        model.addAttribute("employee", employee);
        return "employeeDetail";
    }

    @RequestMapping(value = "/employee/update/{id}")
    public String updateEmployee(
            @PathVariable(value = "id") Long id, Model model) {
        Employee employee = employeeRepository.getById(id);
        model.addAttribute(employee);
        model.addAttribute("companies", companyRepository.findAll());
        return "employeeUpdate";
    }

    @RequestMapping(value = "/employee/save")
    public String saveUpdate(@Valid Employee employee, BindingResult result) {
        if (result.hasErrors()) {
            return "employeeUpdate";
        } else {
            employeeRepository.save(employee);
            return "redirect:/employee/update/" + employee.getId();
        }
    }

    @RequestMapping(value = "/employee/add")
    public String addEmployee(Model model) {
        Employee employee = new Employee();
        model.addAttribute("companies", companyRepository.findAll());
        model.addAttribute("employee", employee);
        return "employeeAdd";
    }

    @RequestMapping(value = "/employee/insert")
    public String insertEmployee(
            Model model, @Valid Employee employee, BindingResult result) {
        if (result.hasErrors()) {
            model.addAttribute("companies", companyRepository.findAll());
            model.addAttribute("employee", employee);
            return "employeeAdd";
        } else {
            employeeRepository.save(employee);
            return "redirect:/employee/" + employee.getId();
        }
    }

    @RequestMapping(value = "/employee/delete/{id}")
    public String deleteEmployee(@PathVariable(value = "id") Long id) {
        if (employeeRepository.findById(id).isPresent()) {
            Employee employee = employeeRepository.findById(id).get();
            employeeRepository.delete(employee);
        }
        return "redirect:/employee/list";
    }
}