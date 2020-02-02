package com.example.marketmaker.common;

import com.sun.javaws.exceptions.InvalidArgumentException;

public class RequestParser implements IRequestParser{

    public IRequest parseFromString(String instruction) throws Exception {

        if (instruction.trim().equalsIgnoreCase("QUIT"))
            return new Request(true);

        String[] instructions = instruction.trim().split(" ");

        if (instructions.length != 3)
            throw new InvalidArgumentException(new String[]{"Instruction format is incorrect: {security ID} (BUY|SELL) {quantity}"});

        int quantity = parseInteger(instructions[2]);
        int securityId = parseInteger(instructions[0]);
        boolean isBuy = parseBuyInstruction(instructions[1]);

        if (securityId <= 0)
            throw new InvalidArgumentException(new String[]{"Security ID cannot be less or equal to 0"});

        if (quantity <= 0)
            throw new InvalidArgumentException(new String[]{"Quantity cannot be less or equal to 0"});

        return new Request(securityId, quantity, isBuy);
    }

    private boolean parseBuyInstruction(String buy) throws InvalidArgumentException {
        switch (buy.toUpperCase()) {
            case "BUY":
                return true;
            case "SELL":
                return false;
            default:
                throw new InvalidArgumentException(new String[]{"Invalid Instruction. (BUY|SELL) only allowed!"});
        }
    }

    private int parseInteger(String securityId) throws InvalidArgumentException {

        try {
            return Integer.parseInt(securityId);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException(new String[]{"Invalid Security ID: Only Numeric values allowed"});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}