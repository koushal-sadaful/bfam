package com.example.marketmaker.common;

public interface IRequestParser {
    IRequest parseFromString(String instruction) throws Exception;
}
