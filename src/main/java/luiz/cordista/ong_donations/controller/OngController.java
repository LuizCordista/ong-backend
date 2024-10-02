package luiz.cordista.ong_donations.controller;

import luiz.cordista.ong_donations.dto.OngDataDTO;
import luiz.cordista.ong_donations.model.Ong;
import luiz.cordista.ong_donations.service.DonationService;
import luiz.cordista.ong_donations.service.OngService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ong")
@CrossOrigin(origins = "*")
public class OngController {

    private final OngService ongService;

    public OngController(OngService ongService) {
        this.ongService = ongService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyOng() {
        try {
            Optional<Ong> authenticatedCustomer = ongService.getAuthenticatedCustomer();
            if (authenticatedCustomer.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }
            OngDataDTO ong = ongService.getOngDetails(authenticatedCustomer.get());
            return ResponseEntity.ok(ong);
        } catch (Exception e) {
            System.out.println("Error searching customer: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
