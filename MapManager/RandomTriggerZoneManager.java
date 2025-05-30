package MapManager;

import gui.TransactionsGUI;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import model.Pembeli;
import model.PerkEffectManager;
import model.Player;

/**
 * Manages random trigger zones with non-overlapping placement
 */
public class RandomTriggerZoneManager {    
    private static final int MIN_ZONES = 3;
    private static final int MAX_ZONES = 8;
    private static final int ZONE_WIDTH = 192;
    private static final int ZONE_HEIGHT = 192;
    private static final int MIN_SPACING = 20; // Minimum spacing between zones
    
    private final List<Rectangle> placedZones;
    private final List<Pembeli> zoneBuyers; // List of Pembeli for each zone
    private TransactionsGUI dialogSystem;
    private Player player;
    
    public RandomTriggerZoneManager() {
        this.placedZones = new ArrayList<>();
        this.zoneBuyers = new ArrayList<>();
        this.dialogSystem = null;
        this.player = null;
    }
    
    /**
     * Set the dialog system for displaying messages
     * @param dialogSystem the DialogSystem instance to use
     */
    public void setDialogSystem(TransactionsGUI dialogSystem) {
        this.dialogSystem = dialogSystem;
    }
    
    /**
     * Set the player for perk-based buyer generation
     */
    public void setPlayer(Player player) {
        this.player = player;
    }
    
    /**
     * Generate random trigger zones within the specified bounds
     * @param minX minimum X coordinate
     * @param maxX maximum X coordinate
     * @param minY minimum Y coordinate
     * @param maxY maximum Y coordinate
     * @param triggerZoneManager the TriggerZoneManager to add zones to
     */
    public void generateRandomZones(int minX, int maxX, int minY, int maxY, TriggerZoneManager triggerZoneManager) {
        placedZones.clear();
        zoneBuyers.clear();
          // Generate random number of zones
        int numZones = (int)(Math.random() * (MAX_ZONES - MIN_ZONES + 1)) + MIN_ZONES;
        System.out.println("Generating " + numZones + " random trigger zones...");
        
        int attemptsPerZone = 50; // Maximum attempts to place each zone
        int successfullyPlaced = 0;
        
        for (int i = 0; i < numZones; i++) {
            boolean placed = false;            for (int attempt = 0; attempt < attemptsPerZone && !placed; attempt++) {
                // Use fixed zone dimensions (192x192)
                int width = ZONE_WIDTH;
                int height = ZONE_HEIGHT;
                  // Generate random position ensuring zone fits within bounds
                int x = (int)(Math.random() * (maxX - minX - width + 1)) + minX;
                int y = (int)(Math.random() * (maxY - minY - height + 1)) + minY;
                
                Rectangle newZone = new Rectangle(x, y, width, height);
                
                // Check if this zone overlaps with any existing zones
                if (!overlapsWithExistingZones(newZone)) {
                    placedZones.add(newZone);
                      // Add to TriggerZoneManager with unique ID
                    String zoneId = "RandomZone_" + (successfullyPlaced + 1);
                    triggerZoneManager.addZone(zoneId, x, y, x + width, y + height, true, () -> {
                        handleRandomZoneTriggered(zoneId, x, y, width, height);
                    });
                      // Generate Pembeli for this zone
                    Pembeli pembeli = (player != null) ? PerkEffectManager.createBuyerWithPerks(player) : Pembeli.buatPembeliAcak();
                    zoneBuyers.add(pembeli);
                    
                    placed = true;
                    successfullyPlaced++;
                    System.out.println("Placed zone " + zoneId + " at (" + x + ", " + y + ") size: " + width + "x" + height);
                }
            }
            
            if (!placed) {
                System.out.println("Failed to place zone " + (i + 1) + " after " + attemptsPerZone + " attempts");
            }
        }
        
        System.out.println("Successfully placed " + successfullyPlaced + " out of " + numZones + " random zones");
    }
    
    /**
     * Check if a new zone overlaps with any existing zones (including spacing)
     */
    private boolean overlapsWithExistingZones(Rectangle newZone) {
        for (Rectangle existingZone : placedZones) {
            // Create expanded rectangle that includes minimum spacing
            Rectangle expandedExisting = new Rectangle(
                existingZone.x - MIN_SPACING,
                existingZone.y - MIN_SPACING,
                existingZone.width + 2 * MIN_SPACING,
                existingZone.height + 2 * MIN_SPACING
            );
            
            if (expandedExisting.intersects(newZone)) {
                return true;
            }
        }
        return false;
    }      /**
     * Handle when a random zone is triggered
     */
    private void handleRandomZoneTriggered(String zoneId, int x, int y, int width, int height) {
        // Find the index of the zone
        int idx = -1;
        for (int i = 0; i < placedZones.size(); i++) {
            Rectangle r = placedZones.get(i);
            if (r.x == x && r.y == y && r.width == width && r.height == height) {
                idx = i;
                break;
            }
        }        Pembeli pembeli = (idx != -1) ? getPembeliForZone(idx) : null;
        if (dialogSystem != null && pembeli != null) {
            dialogSystem.setPembeli(pembeli);
            dialogSystem.showPembeliDialog();
        } else {
            System.out.println("Cannot show dialog: dialogSystem=" + (dialogSystem != null) + ", pembeli=" + (pembeli != null));
        }
        
        // Optional: You can add more effects here like:
        // - Give player random items
        // - Add money
        // - Trigger special events
        // - Play sound effects
    }
    
    /**
     * Get Pembeli for a given zone index
     */
    public Pembeli getPembeliForZone(int index) {
        if (index >= 0 && index < zoneBuyers.size()) {
            return zoneBuyers.get(index);
        }
        return null;
    }
    
    /**
     * Get list of all placed zone rectangles (for debugging)
     */
    public List<Rectangle> getPlacedZones() {
        return new ArrayList<>(placedZones);
    }
    
    /**
     * Clear all placed zones
     */
    public void clearZones() {
        placedZones.clear();
    }
    
    /**
     * Get the number of successfully placed zones
     */
    public int getZoneCount() {
        return placedZones.size();
    }
}
