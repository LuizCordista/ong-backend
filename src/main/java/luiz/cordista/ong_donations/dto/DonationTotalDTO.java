package luiz.cordista.ong_donations.dto;

public record DonationTotalDTO(long totalMonetary, long totalItems, long pendingDonations, long completedDonations,double totalSum) {
}
