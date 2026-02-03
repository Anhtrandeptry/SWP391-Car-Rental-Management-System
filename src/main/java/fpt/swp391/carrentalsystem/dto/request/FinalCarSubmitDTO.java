package fpt.swp391.carrentalsystem.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinalCarSubmitDTO {

    // Dữ liệu từ Step 1
    private Map<String, Object> step1Data;

    // Dữ liệu từ Step 2
    private Map<String, Object> step2Data;

    // Document IDs từ Step 3
    private List<Long> documentIds;

    // Owner ID
    private Long ownerId;
}