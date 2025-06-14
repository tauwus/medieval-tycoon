/*
    AHMAD SYAFI NURROYYAN     (245150201111041)
    HERDY MADANI              (245150207111074)
    NAFISA RAFA ZARIN         (245150200111050)
    NABILLA NUR DIANA SAFITRI (245150207111078)
*/

package model;

import interfaces.Showable;
import interfaces.Upgrade;
import java.util.Objects;

public class Item implements Showable, Upgrade {
    private final String nama;
    private final String deskripsi;
    private final int harga;
    private final int biayaUpgrade;
    private boolean isActive;
    private int level;
    private final String iconPath;
    private boolean isUsed;
    private boolean isConsumable;
    private int quantity;
    private int maxQuantity;

    private static final int MAX_LEVEL = 5;

    public Item(String nama, String deskripsi, int harga, int biayaUpgrade, String iconPath) {
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.harga = harga;
        this.biayaUpgrade = biayaUpgrade;
        this.level = 1;
        this.isActive = false;
        this.iconPath = iconPath;
        this.isUsed = false;
        if (isPeluit()) {
            this.isConsumable = true;
            this.maxQuantity = 10;
            this.quantity = maxQuantity;
        } else {
            this.isConsumable = false;
            this.quantity = 1;
            this.maxQuantity = 1;
        }
    }

    public String getNama() {
        return nama;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public int getHarga() {
        return harga;
    }

    public int getBiayaUpgrade() {
        return biayaUpgrade;
    }

    public String getIconPath() {
        return iconPath;
    }

    public boolean isActive() {
        return isActive;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    @Override
    public int getLevel() {
        return level;
    }

    public boolean isMaxLevel() {
        return level >= MAX_LEVEL;
    }

    public int getMaxLevel() {
        return MAX_LEVEL;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void markAsUsed() {
        this.isUsed = true;
    }

    public void resetUsage() {
        this.isUsed = false;
    }

    public boolean isConsumable() {
        return isConsumable;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = Math.max(0, quantity);
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public boolean canUse() {
        return quantity > 0;
    }

    public boolean consumeOne() {
        if (isConsumable && quantity > 0) {
            quantity--;
            return true;
        }
        return false;
    }

    public void refillQuantity() {
        this.quantity = maxQuantity;
    }

    public double getHipnotisChance() {
        return 0.3 + (level * 0.1);
    }

    public double getJampiMultiplier() {
        return 1.5 + (level * 0.3);
    }

    public double getSemprotenPriceBoost() {
        return 0.15 + (level * 0.05);
    }

    public double getTipBonusRate() {
        return 0.08 + (level * 0.04);
    }

    public int getPeluitExtraBuyers() {
        return level;
    }

    public boolean isHipnotis() {
        return nama.equalsIgnoreCase("Hipnotis");
    }

    public boolean isJampi() {
        return nama.equalsIgnoreCase("Jampi");
    }

    public boolean isSemproten() {
        return nama.equalsIgnoreCase("Semproten");
    }

    public boolean isTip() {
        return nama.equalsIgnoreCase("Tip");
    }

    public boolean isPeluit() {
        return nama.equalsIgnoreCase("Peluit");
    }

    @Override
    public boolean upgradeLevel() {
        if (level < MAX_LEVEL) {
            level++;
            if (isConsumable) {
                maxQuantity = 10 + (level * 5);
                quantity = maxQuantity;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Item item = (Item) o;
        return nama.equals(item.nama);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nama);
    }

    @Override
    public String toString() {
        return String.format("Item[nama=%s, level=%d, aktif=%s]", nama, level, isActive);
    }

    @Override
    public void tampilkanDetail() {
        System.out.println(getDetail());
    }

    public String getDetail() {
        String efekDetail = getEfekDetail();
        return String.format(
                "%s (Lv.%d/%d) - %s\n%s\nHarga: Rp%,d | Upgrade: Rp%,d",
                nama, level, MAX_LEVEL, deskripsi, efekDetail, harga, biayaUpgrade);
    }

    private String getEfekDetail() {
        if (isHipnotis()) {
            return String.format("Efek: %.0f%% chance langsung beli", getHipnotisChance() * 100);
        } else if (isJampi()) {
            return String.format("Efek: %.1fx multiplier penghasilan", getJampiMultiplier());
        } else if (isSemproten()) {
            return String.format("Efek: +%.0f%% harga jual saat transaksi", getSemprotenPriceBoost() * 100);
        } else if (isTip()) {
            return String.format("Efek: %.0f%% chance bonus tip", getTipBonusRate() * 100);
        } else if (isPeluit()) {
            return String.format("Efek: +%d pembeli tambahan", getPeluitExtraBuyers());
        }
        return "Efek tidak diketahui";
    }

    private int peluitUsesToday = 0;
    private int peluitLastUsedDay = -1;

    public int getPeluitDailyLimit() {
        switch (level) {
            case 1:
                return 5;
            case 2:
                return 7;
            case 3:
                return 9;
            case 4:
                return 12;
            case 5:
                return 15;
            default:
                return 5;
        }
    }

    public boolean canUsePeluit(int currentDay) {
        if (!isPeluit())
            return false;
        if (peluitLastUsedDay != currentDay) {
            peluitUsesToday = 0;
            peluitLastUsedDay = currentDay;
        }
        return peluitUsesToday < getPeluitDailyLimit();
    }

    public void incrementPeluitUse(int currentDay) {
        if (!isPeluit())
            return;
        if (peluitLastUsedDay != currentDay) {
            peluitUsesToday = 0;
            peluitLastUsedDay = currentDay;
        }
        peluitUsesToday++;
    }

    public void resetPeluitUsage(int currentDay) {
        if (!isPeluit())
            return;
        peluitUsesToday = 0;
        peluitLastUsedDay = currentDay;
    }

    public int getPeluitUsesToday(int currentDay) {
        if (!isPeluit())
            return 0;
        if (peluitLastUsedDay != currentDay)
            return 0;
        return peluitUsesToday;
    }
}
