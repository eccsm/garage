package com.eccsm.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import com.eccsm.model.Ticket;
import com.eccsm.repository.ITicket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javassist.tools.web.BadHttpRequest;

@Service
@Transactional
public class GarageService {

    private static final int MAX_SIZE = 10;
    private static final int GARAGE_ARRAY = 10;
    private static final int BEFORE_LAST_SLOT = 10;

    @Autowired
    ITicket ticketRepository;

    private String[] size = new String[MAX_SIZE];
    private Map<Ticket, List<Integer>> garageMap = new LinkedHashMap<Ticket, List<Integer>>();

    public String status() {
        String s = new String();
        for (Map.Entry<Ticket, List<Integer>> entry : garageMap.entrySet()) {
            s += entry.getKey().getPlate() + " " + entry.getKey().getColor() + " " + entry.getValue() + "\n";
        }

        return s;

    }

    public Ticket park(Ticket ticket) throws BadHttpRequest {
        List<Integer> slots = new ArrayList<Integer>();
        slots = getEmptySlots(ticket);

        if (slots != null && !slots.isEmpty()) {
            garageMap.put(ticket, slots);
            ticketRepository.save(ticket);
            return ticket;
        }

        throw new RuntimeException("Error Occurred");

    }

    public void leave(Long ticketId) {
        List<String> list = new ArrayList<String>();
        Collections.addAll(list, size);

        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        if (!ticket.isPresent())
            throw new EntityNotFoundException("Ticket can not found");

        if (list.contains(ticket.get().getPlate())) {

            String plate = ticket.get().getPlate();
            for (int i = 0; i < MAX_SIZE - 1; i++) {
                if (size[i] != null) {
                    if (plate.equals(size[i].toString()))
                        size[i] = null;
                }
            }
            ticketRepository.deleteById(ticketId);
            garageMap.remove(ticket.get());
        }

    }

    private List<Integer> getEmptySlots(Ticket ticket) {
        int start = 0, busyLots = 0;
        List<Integer> list = new ArrayList<Integer>();
        List<String> plateList = new ArrayList<String>();
        Collections.addAll(plateList, size);
        
        String regex = "(0[1-9]|[1-7][0-9]|8[01])(([\\-][A-Z][\\-])(\\d{4,5})|([\\-][A-Z][\\-]{2})(\\d{3,4})|([\\-][A-Z][\\-]{3})(\\d{2}))";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(ticket.getPlate());

        if (!matcher.find())
            throw new RuntimeException("Wrong Plate Format");

        if (plateList.contains(ticket.getPlate()))
            throw new RuntimeException("Already Parked");

        List<Ticket> ticketList = ticketRepository.findAll();

        for (Ticket t : ticketList) {
            busyLots += t.getVehicle().getAllocation() + 1;
        }

        int allocation = ticket.getVehicle().getAllocation();

        if (busyLots + allocation > 10)
            throw new RuntimeException("Garage is Full");

        for (int i = 0; i < GARAGE_ARRAY; i++) {
            if (i == BEFORE_LAST_SLOT && start < (allocation - start)) {
                for (int j = start; j > 0; j--) {
                    Integer idx = list.get(j);
                    size[idx - 1] = null;
                }
                start = 0;
                list.clear();
            }

            if (size[i] == null) {
                if (start == allocation)
                    return list;
                if (i == 0) {
                    size[i] = ticket.getPlate();
                    start++;
                    list.add(i + 1);
                } else {
                    if (size[i - 1] == null || ticket.getPlate().equals(size[i - 1])) {
                        size[i] = ticket.getPlate();
                        start++;
                        list.add(i + 1);
                    }
                }
            } else {
                if (start == allocation)
                    return list;
                else {
                    for (int j = start; j > 0; j--) {
                        Integer idx = list.get(j);
                        size[idx - 1] = null;
                    }
                    start = 0;
                    list.clear();
                }
            }

        }
        return list;
    }
}
