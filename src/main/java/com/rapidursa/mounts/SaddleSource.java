package com.rapidursa.mounts;

public enum SaddleSource
{
    BANK_BUFFALO("Bank buffalo"),
    CART_CAMEL("Cart camel"),
    MOUNTED_TERRORBIRD("Mounted terrorbird");

    private final String displayName;

    SaddleSource(String displayName)
    {
        this.displayName = displayName;
    }

    @Override
    public String toString()
    {
        return displayName;
    }
}
