package boerenkool.communication.controller;

import boerenkool.business.service.HouseService;
import boerenkool.communication.dto.HouseDetailsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HouseDetailController {

    private final HouseService houseService;

    @Autowired
    public HouseDetailController(HouseService houseService) {
        this.houseService = houseService;
    }

    @GetMapping("/huisje")
    public String showHouseDetail(@RequestParam("id") int houseId,
                                  @RequestParam("naam") String houseName,
                                  Model model) {
        //
        HouseDetailsDTO houseDetails = houseService.getOneByIdAndConvertToDTO(houseId);

        if (houseDetails == null) {

            return "error/404";
        }


        String expectedName = houseDetails.getHouseName().toLowerCase().replace(" ", "-");
        if (!expectedName.equals(houseName.toLowerCase())) {

            return "redirect:/huisje?id=" + houseId + "&naam=" + expectedName;
        }


        model.addAttribute("house", houseDetails);


        return "huisdetail";
    }
}
