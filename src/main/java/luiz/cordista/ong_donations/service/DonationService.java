package luiz.cordista.ong_donations.service;

import luiz.cordista.ong_donations.dto.DonationItemResponseDTO;
import luiz.cordista.ong_donations.dto.DonationMonetaryResponseDTO;
import luiz.cordista.ong_donations.dto.DonationRequestDTO;
import luiz.cordista.ong_donations.enums.DonationType;
import luiz.cordista.ong_donations.enums.Status;
import luiz.cordista.ong_donations.model.Donation;
import luiz.cordista.ong_donations.model.Ong;
import luiz.cordista.ong_donations.repository.DonationRepository;
import luiz.cordista.ong_donations.repository.OngRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DonationService {

    private final DonationRepository donationRepository;
    private final OngRepository ongRepository;

    public DonationService(DonationRepository donationRepository, OngRepository ongRepository) {
        this.donationRepository = donationRepository;
        this.ongRepository = ongRepository;
    }

    public Object createDonation(DonationRequestDTO donationRequestDTO, Ong ong) {

        Donation donation = new Donation();
        donation.setDonorName(donationRequestDTO.donorName());
        donation.setDonationType(donationRequestDTO.donationType());
        donation.setAmount(donationRequestDTO.amount());
        donation.setItemDescription(donationRequestDTO.itemDescription());
        donation.setStatus(donationRequestDTO.status());
        donation.setOng(ong);
        donation.setCreatedDate(Date.from(ZonedDateTime.now(ZoneId.of("GMT-3")).toInstant()));

        donation = donationRepository.save(donation);

        if (donationRequestDTO.donationType() == DonationType.MONETARY) {
            ong.setTotalDonations(ong.getTotalDonations() + donation.getAmount());
            return new DonationMonetaryResponseDTO(donation.getId(), donation.getDonationType(),donation.getDonorName(), donation.getAmount(), donation.getStatus(), donation.getCreatedDate());
        }

        if (donationRequestDTO.donationType() == DonationType.ITEM) {
            return new DonationItemResponseDTO(donation.getId(), donation.getDonationType(), donation.getDonorName(), donation.getItemDescription(), donation.getStatus(), donation.getCreatedDate());
        }

        return null;
    }

    public List<Object> getAllDonationsByOng(Ong ong) {
        List<Donation> donations = donationRepository.findByOng(ong);
        return donations.stream().map(donation -> {
            if (donation.getDonationType() == DonationType.MONETARY) {
                return new DonationMonetaryResponseDTO(donation.getId(), donation.getDonationType(), donation.getDonorName(), donation.getAmount(), donation.getStatus(), donation.getCreatedDate());
            } else {
                return new DonationItemResponseDTO(donation.getId(), donation.getDonationType(), donation.getDonorName(), donation.getItemDescription(), donation.getStatus(), donation.getCreatedDate());
            }
        }).collect(Collectors.toList());
    }

    public Object getDonationById(String donationId, Ong ong) {
        Donation donation = donationRepository.findById(donationId).orElse(null);
        if (donation == null) {
            return null;
        }

        if (donation.getOng() != ong) {
            return null;
        }
        if (donation.getDonationType() == DonationType.MONETARY) {
            return new DonationMonetaryResponseDTO(donation.getId(), donation.getDonationType(), donation.getDonorName(), donation.getAmount(), donation.getStatus(), donation.getCreatedDate());
        } else {
            return new DonationItemResponseDTO(donation.getId(), donation.getDonationType(), donation.getDonorName(), donation.getItemDescription(), donation.getStatus(), donation.getCreatedDate());
        }
    }

    public void deleteDonation(String id, Ong ong) {
        Donation donation = donationRepository.findById(id).orElse(null);
        if (donation == null) {
            return;
        }
        if (donation.getDonationType() == DonationType.MONETARY) {
            ong.setTotalDonations(ong.getTotalDonations() - donation.getAmount());
        }

        if (donation.getOng() != ong) {
            return;
        }
        donationRepository.deleteById(id);
    }

    public Object editDonation(String id, DonationRequestDTO donationRequestDTO, Ong ong) {
        Donation donation = donationRepository.findById(id).orElse(null);
        if (donation == null) {
            return null;
        }
        if (donation.getOng() != ong) {
            return null;
        }

        if (donation.getDonationType() == DonationType.MONETARY) {
            ong.setTotalDonations(ong.getTotalDonations() - donation.getAmount());
        }

        donation.setDonorName(donationRequestDTO.donorName());
        donation.setDonationType(donationRequestDTO.donationType());
        donation.setAmount(donationRequestDTO.amount());
        donation.setItemDescription(donationRequestDTO.itemDescription());
        donation.setStatus(donationRequestDTO.status());
        donation.setOng(ong);

        donation = donationRepository.save(donation);

        if (donationRequestDTO.donationType() == DonationType.MONETARY) {
            ong.setTotalDonations(ong.getTotalDonations() + donation.getAmount());
            return new DonationMonetaryResponseDTO(donation.getId(), donation.getDonationType(), donation.getDonorName(), donation.getAmount(), donation.getStatus(), donation.getCreatedDate());
        }

        return new DonationItemResponseDTO(donation.getId(), donation.getDonationType(), donation.getDonorName(), donation.getItemDescription(), donation.getStatus(), donation.getCreatedDate());
    }

    public Object editDonationStatus(String id, String status, Ong ong) {
        Donation donation = donationRepository.findById(id).orElse(null);
        if (donation == null) {
            return null;
        }
        if (donation.getOng() != ong) {
            return null;
        }

        donation.setStatus(Status.valueOf(status));
        donation = donationRepository.save(donation);

        if (donation.getDonationType() == DonationType.MONETARY) {
            return new DonationMonetaryResponseDTO(donation.getId(), donation.getDonationType(), donation.getDonorName(), donation.getAmount(), donation.getStatus(), donation.getCreatedDate());
        } else {
            return new DonationItemResponseDTO(donation.getId(), donation.getDonationType(), donation.getDonorName(), donation.getItemDescription(), donation.getStatus(), donation.getCreatedDate());
        }
    }

    public List<Object> getFilteredDonations(Date startDate, Date endDate, Status status, DonationType type, Ong ong) {
        Stream<Donation> donationStream = donationRepository.findByOng(ong).stream();

        if (startDate != null) {
            donationStream = donationStream.filter(donation -> !donation.getCreatedDate().before(startDate));
        }
        if (endDate != null) {
            donationStream = donationStream.filter(donation -> !donation.getCreatedDate().after(endDate));
        }
        if (status != null) {
            donationStream = donationStream.filter(donation -> donation.getStatus() == status);
        }
        if (type != null) {
            donationStream = donationStream.filter(donation -> donation.getDonationType() == type);
        }

        return donationStream.map(donation -> {
            if (donation.getDonationType() == DonationType.MONETARY) {
                return new DonationMonetaryResponseDTO(donation.getId(), donation.getDonationType(), donation.getDonorName(), donation.getAmount(), donation.getStatus(), donation.getCreatedDate());
            } else {
                return new DonationItemResponseDTO(donation.getId(), donation.getDonationType(), donation.getDonorName(), donation.getItemDescription(), donation.getStatus(), donation.getCreatedDate());
            }
        }).collect(Collectors.toList());
    }

    public byte[] exportDonationsToExcel(Ong ong) throws IOException {
        List<Donation> donations = donationRepository.findByOng(ong);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Donations");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Donor Name", "Donation Type", "Description", "Status", "Date"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (Donation donation : donations) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(donation.getId());
                row.createCell(1).setCellValue(donation.getDonorName());
                row.createCell(2).setCellValue(donation.getDonationType().toString());
                row.createCell(3).setCellValue(donation.getItemDescription());
                row.createCell(4).setCellValue(donation.getStatus().toString());
                row.createCell(5).setCellValue(donation.getCreatedDate().toString());
            }

            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    public long countItemDonations(Ong ong) {
        return donationRepository.findByOng(ong).stream()
                .filter(donation -> donation.getDonationType() == DonationType.ITEM)
                .count();
    }

    public long countMonetaryDonations(Ong ong) {
        return donationRepository.findByOng(ong).stream()
                .filter(donation -> donation.getDonationType() == DonationType.MONETARY)
                .count();
    }

    public double sumMonetaryDonations(Ong ong) {
        return donationRepository.findByOng(ong).stream()
                .filter(donation -> donation.getDonationType() == DonationType.MONETARY)
                .mapToDouble(Donation::getAmount)
                .sum();
    }

    public long countPendingDonations(Ong ong) {
        return donationRepository.findByOng(ong).stream()
                .filter(donation -> donation.getStatus() == Status.PENDING)
                .count();
    }

    public long countCompletedDonations(Ong ong) {
        return donationRepository.findByOng(ong).stream()
                .filter(donation -> donation.getStatus() == Status.COMPLETED)
                .count();
    }
}
