package luiz.cordista.ong_donations.dto;

import luiz.cordista.ong_donations.enums.DonationType;
import luiz.cordista.ong_donations.enums.Status;

public record DonationRequestDTO(String donorName, DonationType donationType, float amount, String itemDescription, Status status) {
}
