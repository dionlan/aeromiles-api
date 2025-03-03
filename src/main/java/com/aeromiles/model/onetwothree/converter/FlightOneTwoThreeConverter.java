package com.aeromiles.model.onetwothree.converter;

import com.aeromiles.model.maxmilhas.dto.AirportDTO;
import com.aeromiles.model.onetwothree.FlightOneTwoThree;
import com.aeromiles.model.onetwothree.dto.AirlineOneTwoThreeDTO;
import com.aeromiles.model.onetwothree.dto.FlightOneTwoThreeDTO;
import com.aeromiles.model.onetwothree.dto.FlightOneTwoThreeResponseDTO;
import com.aeromiles.util.DateUtil;

import java.util.Arrays;
import java.util.List;

public class FlightOneTwoThreeConverter {

    public FlightOneTwoThreeResponseDTO.FlightDTO convertToDTOResponse(FlightOneTwoThreeDTO flightDTO){
        FlightOneTwoThreeResponseDTO.FlightDTO flight = new FlightOneTwoThreeResponseDTO.FlightDTO();
        flight.set_id(flightDTO.getUniqueId());
        flight.setAirlineLogo(flightDTO.getAirline().getAirlineLogo());
        flight.setAirlineName(flightDTO.getAirline().getName());
        flight.setStopType(flightDTO.getEscale());
        flight.setRefundableStatus("");

        FlightOneTwoThreeResponseDTO.FlightInfoDTO flightInfoDTO = new FlightOneTwoThreeResponseDTO.FlightInfoDTO();
        flightInfoDTO.setFlightNumber(flightDTO.getFlightNumber());
        flightInfoDTO.setAircraft("");
        flightInfoDTO.setOperatedBy(flightDTO.getAirline().getCodeCia());
        flightInfoDTO.setClassType(flightDTO.getClassService().equals(3) ? "Econômica" : "Executiva");
        flightInfoDTO.setBaggage("20 Kg(s)");
        flightInfoDTO.setCheckIn("20 Kg(s)");
        flightInfoDTO.setCabin("7 Ks(s)");
        flight.setFlightInfo(flightInfoDTO);

        List<FlightOneTwoThreeResponseDTO.CancellationRuleDTO> cancellationRuleDTO = Arrays.asList(new FlightOneTwoThreeResponseDTO.CancellationRuleDTO("Maxinum penalty amount", 50));
        flight.setCancellationRules(cancellationRuleDTO);

        List<FlightOneTwoThreeResponseDTO.DateChangeRuleDTO> dateChangeRuleDTOS = Arrays.asList(new FlightOneTwoThreeResponseDTO.DateChangeRuleDTO("Maxinum penalty amount", 30));
        flight.setDateChangeRules(dateChangeRuleDTOS);

        String [] notes = {"*Note For Voluntary Cancellation: Service fees & gateway charges will be additionally applied on top of penalty amount. Some taxes may not be refundable\",\"*Note For Date Change: Service Fees, Fare difference and Tax difference will be additionally applied on top of penalty amount which is nonrefundable in any circumstances."};
        flight.setNotes(List.of(notes));

        flight.setDuration(FlightOneTwoThreeDTO.setDurationFormatado(flightDTO.getTotalFlightDuration()));

        flight.setDateChangeRules(dateChangeRuleDTOS);

        FlightOneTwoThreeResponseDTO.AvailableSeatsDTO availableSeatsDTO = new FlightOneTwoThreeResponseDTO.AvailableSeatsDTO();
        availableSeatsDTO.setFlightId(flightDTO.getUniqueId());
        availableSeatsDTO.setTotalSeat("119");
        availableSeatsDTO.setAvailable("1");
        List<FlightOneTwoThreeResponseDTO.SeatDTO> seats = Arrays.asList(new FlightOneTwoThreeResponseDTO.SeatDTO("A1", true));
        availableSeatsDTO.setSeats(seats);
        flight.setAvailableSeats(availableSeatsDTO);

        FlightOneTwoThreeResponseDTO.DepartureDTO departureDTO = new FlightOneTwoThreeResponseDTO.DepartureDTO();
        departureDTO.setCode(flightDTO.getDepartureLocation());
        departureDTO.setDate(DateUtil.stringToLocalDateString(flightDTO.getDepartureTime()));
        departureDTO.setTime(DateUtil.stringToTimeString(flightDTO.getDepartureTime()));
        departureDTO.setCity(flightDTO.getDepartureAirport().getCity());
        departureDTO.setTerminal("B");
        departureDTO.setAirportName(flightDTO.getDepartureAirport().getName());
        departureDTO.setSeats("120");
        flight.setDeparture(departureDTO);

        FlightOneTwoThreeResponseDTO.ArrivalDTO arrivalDTO = new FlightOneTwoThreeResponseDTO.ArrivalDTO();
        arrivalDTO.setCode(flightDTO.getArrivalLocation());
        arrivalDTO.setDate(DateUtil.stringToLocalDateString(flightDTO.getArrivalTime()));
        arrivalDTO.setTime(DateUtil.stringToTimeString(flightDTO.getArrivalTime()));
        arrivalDTO.setCity(flightDTO.getArrivalAirport().getCity());
        arrivalDTO.setTerminal("B");
        arrivalDTO.setAirportName(flightDTO.getArrivalAirport().getName());
        arrivalDTO.setSeats("120");
        flight.setArrival(arrivalDTO);

        FlightOneTwoThreeResponseDTO.FareSummaryDTO fareSummaryDTO = new FlightOneTwoThreeResponseDTO.FareSummaryDTO();
        fareSummaryDTO.setBaseFare(flightDTO.getTax());
        fareSummaryDTO.setTaxesAndFees(flightDTO.getTax());
        fareSummaryDTO.setTotal(flightDTO.getTotalPrice());
        fareSummaryDTO.setMiles(flightDTO.getMiles());
        flight.setFareSummary(fareSummaryDTO);

        return flight;
    }

    public FlightOneTwoThreeDTO convertToDTO(FlightOneTwoThree flight) {
        return FlightOneTwoThreeDTO.builder()
            .uniqueId(flight.getUniqueId())
            .key(flight.getKey())
            .uniqueIdClean(flight.getUniqueIdClean())
            .luggageQuantity(flight.getLuggageQuantity())
            .flightNumber(flight.getFlightNumber())
            .classService(flight.getClassService())
            .departureTime(DateUtil.localDateTimeToString(flight.getDepartureTime()))
            .departureLocation(flight.getDepartureLocation())
            .arrivalLocation(flight.getArrivalLocation())
            .arrivalTime(DateUtil.localDateTimeToString(flight.getArrivalTime()))
            .arrivalRegion(flight.getArrivalRegion())
            .departureRegion(flight.getDepartureRegion())
            .priceMilesVip(flight.getPriceMilesVip())
            .ourPrice(flight.getOurPrice())
            .ourPriceInfant(flight.getOurPriceInfant())
            .airlinePrice(flight.getAirlinePrice())
            .bestAirlinePrice(flight.getBestAirlinePrice())
            .awardPrice(flight.getAwardPrice())
            .fare123Milhas(flight.getFare123Milhas())
            .airlinePriceInfant(flight.getAirlinePriceInfant())
            .tax(flight.getTax())
            .totalTax(flight.getTotalTax())
            .economy(flight.getEconomy())
            .totalPrice(flight.getTotalPrice())
            .qntEscale(flight.getQntEscale())
            .qntConnection(flight.getQntConnection())
            .qntStop(flight.getQntStop())
            .escale(flight.getEscale())
            .daysBetweenDepartureAndArrival(flight.getDaysBetweenDepartureAndArrival())
            .totalFlightDuration(flight.getTotalFlightDuration())
            .tariffedBaby(flight.getTariffedBaby())
            .parserName(flight.getParserName())
            .handLuggage(flight.getHandLuggage())
            .isFareBaseWithDiscount(flight.getIsFareBaseWithDiscount())
            .warningEscale(flight.getWarningEscale())
            .semiExecutive(flight.getSemiExecutive())
            .miles(flight.getMiles())
            .airline(AirlineOneTwoThreeDTO.toDTO(flight.getAirlineOneTwoThree()))
            .departureAirport(AirportDTO.toDTO(flight.getDepartureAirport()))
            .arrivalAirport(AirportDTO.toDTO(flight.getArrivalAirport()))
            .build();
    }
}
