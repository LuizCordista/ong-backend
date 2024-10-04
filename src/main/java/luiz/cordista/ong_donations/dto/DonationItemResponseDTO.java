package luiz.cordista.ong_donations.dto;

import luiz.cordista.ong_donations.enums.DonationType;
import luiz.cordista.ong_donations.enums.Status;

import java.util.Date;

public record DonationItemResponseDTO(
        String id,
        DonationType donationType,
        String donorName,
        String description,
        Status status,
        Date date) {
}
