package luiz.cordista.ong_donations.dto;

import luiz.cordista.ong_donations.enums.DonationType;
import luiz.cordista.ong_donations.enums.Status;

import java.util.Date;

public record DonationMonetaryResponseDTO(String id, DonationType donationType, String donorName, float amount, Status status, Date date) {
}
