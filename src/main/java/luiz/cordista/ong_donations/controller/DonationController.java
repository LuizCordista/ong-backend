package luiz.cordista.ong_donations.controller;

import luiz.cordista.ong_donations.dto.DonationRequestDTO;
import luiz.cordista.ong_donations.dto.DonationTotalDTO;
import luiz.cordista.ong_donations.enums.DonationType;
import luiz.cordista.ong_donations.enums.Status;
import luiz.cordista.ong_donations.model.Donation;
import luiz.cordista.ong_donations.model.Ong;
import luiz.cordista.ong_donations.service.DonationService;
import luiz.cordista.ong_donations.service.OngService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/donation")
@CrossOrigin(origins = "*")
public class DonationController {

    private final DonationService donationService;
    private final OngService ongService;

    public DonationController(DonationService donationService, OngService ongService) {
        this.donationService = donationService;
        this.ongService = ongService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createDonation(@RequestBody DonationRequestDTO donationRequestDTO) {
        try {
            Optional<Ong> authenticatedCustomer = ongService.getAuthenticatedCustomer();
            if (authenticatedCustomer.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }
            Object donation = donationService.createDonation(donationRequestDTO, authenticatedCustomer.get());
            return ResponseEntity.ok(donation);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllDonations() {
        try {
            Optional<Ong> authenticatedCustomer = ongService.getAuthenticatedCustomer();
            if (authenticatedCustomer.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }
            List<Object> donations = donationService.getAllDonationsByOng(authenticatedCustomer.get());
            return ResponseEntity.ok(donations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDonationById(@PathVariable String id) {
        try {
            Optional<Ong> authenticatedCustomer = ongService.getAuthenticatedCustomer();
            if (authenticatedCustomer.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }
            Object donation = donationService.getDonationById(id, authenticatedCustomer.get());
            return ResponseEntity.ok(donation);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @GetMapping("/report")
    public ResponseEntity<?> getDonationReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date start_date,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date end_date,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) DonationType type) {
        try {
            Optional<Ong> authenticatedCustomer = ongService.getAuthenticatedCustomer();
            if (authenticatedCustomer.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }
            List<Object> donations = donationService.getFilteredDonations(
                    start_date,
                    end_date,
                    status,
                    type,
                    authenticatedCustomer.get()
            );
            return ResponseEntity.ok(donations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportDonationsToExcel() {
        try {
            Optional<Ong> authenticatedCustomer = ongService.getAuthenticatedCustomer();
            if (authenticatedCustomer.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
            byte[] excelContent = donationService.exportDonationsToExcel(authenticatedCustomer.get());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "doacoes.xlsx");
            return new ResponseEntity<>(excelContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/total")
    public ResponseEntity<?> getTotalDonations() {
        try {
            Optional<Ong> authenticatedCustomer = ongService.getAuthenticatedCustomer();
            if (authenticatedCustomer.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }
            long totalItems = donationService.countItemDonations(authenticatedCustomer.get());
            long totalMonetary = donationService.countMonetaryDonations(authenticatedCustomer.get());
            long pendingDonations = donationService.countPendingDonations(authenticatedCustomer.get());
            long completedDonations = donationService.countCompletedDonations(authenticatedCustomer.get());
            double totalSum = donationService.sumMonetaryDonations(authenticatedCustomer.get());


            DonationTotalDTO totalDonations = new DonationTotalDTO(
                    totalMonetary,
                    totalItems,
                    pendingDonations,
                    completedDonations,
                    totalSum);

            return ResponseEntity.ok(totalDonations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDonation(@PathVariable String id, @RequestBody DonationRequestDTO donationRequestDTO) {
        try {
            Optional<Ong> authenticatedCustomer = ongService.getAuthenticatedCustomer();
            if (authenticatedCustomer.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }
            Object donation = donationService.editDonation(id, donationRequestDTO, authenticatedCustomer.get());
            return ResponseEntity.ok(donation);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateDonationStatus(@PathVariable String id, @RequestParam String status) {
        try {
            Optional<Ong> authenticatedCustomer = ongService.getAuthenticatedCustomer();
            if (authenticatedCustomer.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }
            Object donation = donationService.editDonationStatus(id, status.toUpperCase(), authenticatedCustomer.get());
            return ResponseEntity.ok(donation);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDonation(@PathVariable String id) {
        try {
            Optional<Ong> authenticatedCustomer = ongService.getAuthenticatedCustomer();
            if (authenticatedCustomer.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }
            donationService.deleteDonation(id, authenticatedCustomer.get());
            return ResponseEntity.ok("Donation deleted");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
}
