package fpt.swp391.carrentalsystem.controller.owner;

import fpt.swp391.carrentalsystem.entity.User;
import fpt.swp391.carrentalsystem.security.CustomUserDetails;
import fpt.swp391.carrentalsystem.service.CarImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("/owner/cars")
@RequiredArgsConstructor
public class OwnerCarImageController {

    private final CarImageService carImageService;

    @GetMapping("/{carId}/images")
    public String manageImages(@PathVariable Long carId, Model model) {
        User owner = getCurrentUser();
        model.addAttribute("carId", carId);
        model.addAttribute("images", carImageService.getImagesByCar(carId, owner));
        return "owner/car-images";
    }

    @PostMapping("/{carId}/images/upload")
    public String uploadImages(@PathVariable Long carId,
                               @RequestParam("files") List<MultipartFile> files) throws Exception {
        carImageService.uploadImages(carId, getCurrentUser(), files);
        return "redirect:/owner/cars/" + carId + "/images";
    }

    @PostMapping("/images/{imageId}/set-main")
    public String setMain(@PathVariable Long imageId, @RequestParam Long carId) {
        carImageService.setMainImage(imageId, getCurrentUser());
        return "redirect:/owner/cars/" + carId + "/images";
    }

    @PostMapping("/images/{imageId}/delete")
    public String delete(@PathVariable Long imageId, @RequestParam Long carId) {
        carImageService.deleteImage(imageId, getCurrentUser());
        return "redirect:/owner/cars/" + carId + "/images";
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ((CustomUserDetails) auth.getPrincipal()).getUser();
    }
}
