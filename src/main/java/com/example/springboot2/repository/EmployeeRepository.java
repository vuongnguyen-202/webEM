package com.example.springboot2.repository;

import com.example.springboot2.model.Company;
import com.example.springboot2.model.Employee;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByCompany(Company company, Sort sort);

}



