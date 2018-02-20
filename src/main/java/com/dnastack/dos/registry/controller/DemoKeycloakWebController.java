package com.dnastack.dos.registry.controller;

import com.dnastack.dos.registry.model.Customer;
import com.dnastack.dos.registry.repository.CustomerRepository;
import com.dnastack.dos.registry.service.DataNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

/**
 * This class servers as ...
 *
 * @Author: marchuang <br/>
 * @since: 1.0.0 <br/>
 */
@Controller
public class DemoKeycloakWebController {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DataNodeService dataNodeService;

    @GetMapping(path = "/")
    public String index() {
        return "external";
    }

    @GetMapping(path = "/customers")
    public String customers(Principal principal, Model model) {
        addCustomers();
        Iterable<Customer> customers = customerRepository.findAll();
        model.addAttribute("customers", customers);
        model.addAttribute("username", principal.getName());
        return "customers";
    }

    @GetMapping(path = "/nodes")
    public String nodes(Principal principal, Model model) {

        List<Ga4ghDataNodeDto> nodes = dataNodeService.getNodes(null, null, null, null, null, null, null);
        model.addAttribute("nodes", nodes);
        model.addAttribute("username", principal.getName());
        return "nodes";
    }


    // add customers for demonstration
    public void addCustomers() {

        Customer customer1 = new Customer();
        customer1.setAddress("1111 foo blvd");
        customer1.setName("Foo Industries");
        customer1.setServiceRendered("Important services");
        customerRepository.save(customer1);

        Customer customer2 = new Customer();
        customer2.setAddress("2222 bar street");
        customer2.setName("Bar LLP");
        customer2.setServiceRendered("Important services");
        customerRepository.save(customer2);

        Customer customer3 = new Customer();
        customer3.setAddress("33 main street");
        customer3.setName("Big LLC");
        customer3.setServiceRendered("Important services");
        customerRepository.save(customer3);
    }
}
