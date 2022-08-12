package com.al3xkras.messenger.dto;

public class PageRequestDto {
    private int page;
    private int size;

    public PageRequestDto(){

    }
    //<editor-fold defaultstate="collapsed" desc="delombok">
    @SuppressWarnings("all")
    public int getPage() {
        return this.page;
    }

    @SuppressWarnings("all")
    public int getSize() {
        return this.size;
    }

    @SuppressWarnings("all")
    public void setPage(final int page) {
        this.page = page;
    }

    @SuppressWarnings("all")
    public void setSize(final int size) {
        this.size = size;
    }

    @Override
    @SuppressWarnings("all")
    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof PageRequestDto)) return false;
        final PageRequestDto other = (PageRequestDto) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getPage() != other.getPage()) return false;
        if (this.getSize() != other.getSize()) return false;
        return true;
    }

    @SuppressWarnings("all")
    protected boolean canEqual(final Object other) {
        return other instanceof PageRequestDto;
    }

    @Override
    @SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        result = result * PRIME + this.getPage();
        result = result * PRIME + this.getSize();
        return result;
    }

    @Override
    @SuppressWarnings("all")
    public String toString() {
        return "PageRequestDto(page=" + this.getPage() + ", size=" + this.getSize() + ")";
    }

    @SuppressWarnings("all")
    public PageRequestDto(final int page, final int size) {
        this.page = page;
        this.size = size;
    }
    //</editor-fold>
}
