package luiz.cordista.ong_donations.dto;

public record LoginResponseDTO(String jwt,
                               long totalMonetary,
                               long totalItems,
                               long pendingDonations,
                               long completedDonations,
                               double totalSum) {
}
