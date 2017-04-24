package xyz.smirking.script;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreeperPowerEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.entity.EntityPortalExitEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.HorseJumpEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PigZapEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.SheepDyeWoolEvent;
import org.bukkit.event.entity.SheepRegrowWoolEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerChannelEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerInventoryEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.event.player.PlayerUnregisterChannelEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.RemoteServerCommandEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.event.server.ServiceUnregisterEvent;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.SpawnChangeEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class ScriptPlugin extends JavaPlugin {
    private static Set<Class<? extends Event>> bukkitEvents = ImmutableSet.of(
            AsyncPlayerChatEvent.class,
            AsyncPlayerPreLoginEvent.class,
            BlockBreakEvent.class,
            BlockBurnEvent.class,
            BlockCanBuildEvent.class,
            BlockDamageEvent.class,
            BlockDispenseEvent.class,
            BlockExpEvent.class,
            BlockFormEvent.class,
            BlockFadeEvent.class,
            BlockFromToEvent.class,
            BlockGrowEvent.class,
            BlockIgniteEvent.class,
            BlockPhysicsEvent.class,
            BlockPistonExtendEvent.class,
            BlockPistonRetractEvent.class,
            BlockPlaceEvent.class,
            BlockRedstoneEvent.class,
            BlockSpreadEvent.class,
            BrewEvent.class,
            ChunkLoadEvent.class,
            ChunkPopulateEvent.class,
            ChunkUnloadEvent.class,
            CraftItemEvent.class,
            CreatureSpawnEvent.class,
            CreeperPowerEvent.class,
            EnchantItemEvent.class,
            EntityBlockFormEvent.class,
            EntityBreakDoorEvent.class,
            EntityChangeBlockEvent.class,
            EntityCombustByBlockEvent.class,
            EntityCombustByEntityEvent.class,
            EntityCombustEvent.class,
            EntityCreatePortalEvent.class,
            EntityDamageByBlockEvent.class,
            EntityDamageByEntityEvent.class,
            EntityDamageEvent.class,
            EntityDeathEvent.class,
            EntityExplodeEvent.class,
            EntityInteractEvent.class,
            EntityPortalEnterEvent.class,
            EntityPortalEvent.class,
            EntityPortalExitEvent.class,
            EntityRegainHealthEvent.class,
            EntityShootBowEvent.class,
            EntityTameEvent.class,
            EntityTargetEvent.class,
            EntityTargetLivingEntityEvent.class,
            EntityTeleportEvent.class,
            EntityUnleashEvent.class,
            ExpBottleEvent.class,
            ExplosionPrimeEvent.class,
            FoodLevelChangeEvent.class,
            FurnaceBurnEvent.class,
            FurnaceExtractEvent.class,
            FurnaceSmeltEvent.class,
            HangingBreakByEntityEvent.class,
            HangingBreakEvent.class,
            HangingPlaceEvent.class,
            HorseJumpEvent.class,
            InventoryClickEvent.class,
            InventoryCloseEvent.class,
            InventoryCreativeEvent.class,
            InventoryDragEvent.class,
            InventoryInteractEvent.class,
            InventoryMoveItemEvent.class,
            InventoryOpenEvent.class,
            InventoryPickupItemEvent.class,
            ItemDespawnEvent.class,
            ItemSpawnEvent.class,
            LeavesDecayEvent.class,
            LightningStrikeEvent.class,
            MapInitializeEvent.class,
            NotePlayEvent.class,
            PigZapEvent.class,
            PlayerAchievementAwardedEvent.class,
            PlayerAnimationEvent.class,
            PlayerBedEnterEvent.class,
            PlayerBedLeaveEvent.class,
            PlayerBucketEmptyEvent.class,
            PlayerBucketFillEvent.class,
            PlayerChangedWorldEvent.class,
            PlayerChannelEvent.class,
            PlayerChatEvent.class,
            PlayerChatTabCompleteEvent.class,
            PlayerCommandPreprocessEvent.class,
            PlayerDeathEvent.class,
            PlayerDropItemEvent.class,
            PlayerEditBookEvent.class,
            PlayerEggThrowEvent.class,
            PlayerExpChangeEvent.class,
            PlayerFishEvent.class,
            PlayerGameModeChangeEvent.class,
            PlayerInteractAtEntityEvent.class,
            PlayerInteractEntityEvent.class,
            PlayerInteractEvent.class,
            PlayerInventoryEvent.class,
            PlayerItemBreakEvent.class,
            PlayerItemConsumeEvent.class,
            PlayerItemHeldEvent.class,
            PlayerJoinEvent.class,
            PlayerKickEvent.class,
            PlayerLeashEntityEvent.class,
            PlayerLevelChangeEvent.class,
            PlayerLoginEvent.class,
            PlayerMoveEvent.class,
            PlayerPickupItemEvent.class,
            PlayerPortalEvent.class,
            PlayerPreLoginEvent.class,
            PlayerQuitEvent.class,
            PlayerRegisterChannelEvent.class,
            PlayerRespawnEvent.class,
            PlayerShearEntityEvent.class,
            PlayerStatisticIncrementEvent.class,
            PlayerTeleportEvent.class,
            PlayerToggleFlightEvent.class,
            PlayerToggleSneakEvent.class,
            PlayerToggleSprintEvent.class,
            PlayerUnleashEntityEvent.class,
            PlayerUnregisterChannelEvent.class,
            PlayerVelocityEvent.class,
            PluginDisableEvent.class,
            PluginEnableEvent.class,
            PortalCreateEvent.class,
            PotionSplashEvent.class,
            PrepareItemCraftEvent.class,
            PrepareItemEnchantEvent.class,
            ProjectileHitEvent.class,
            ProjectileLaunchEvent.class,
            RemoteServerCommandEvent.class,
            ServerCommandEvent.class,
            ServerListPingEvent.class,
            ServiceRegisterEvent.class,
            ServiceUnregisterEvent.class,
            SheepDyeWoolEvent.class,
            SheepRegrowWoolEvent.class,
            SignChangeEvent.class,
            SlimeSplitEvent.class,
            SpawnChangeEvent.class,
            StructureGrowEvent.class,
            ThunderChangeEvent.class,
            VehicleBlockCollisionEvent.class,
            VehicleCreateEvent.class,
            VehicleDamageEvent.class,
            VehicleDestroyEvent.class,
            VehicleEnterEvent.class,
            VehicleEntityCollisionEvent.class,
            VehicleExitEvent.class,
            VehicleMoveEvent.class,
            VehicleUpdateEvent.class,
            WeatherChangeEvent.class,
            WorldInitEvent.class,
            WorldLoadEvent.class,
            WorldSaveEvent.class,
            WorldUnloadEvent.class
    );

    private final Map<Path, ScriptInterface> interfaces = Maps.newHashMap();
    private final Map<String, String> eventNames = Maps.newHashMap();
    private ScriptEngine scriptEngine;
    private WatchKey watchKey;

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        getServer().getScheduler().cancelTasks(this);

        if (scriptEngine != null) {
            scriptEngine = null;
        }

        if (watchKey != null && watchKey.isValid()) {
            watchKey.cancel();
            watchKey = null;
        }

        interfaces.values().forEach(ScriptInterface::cleanup);
        eventNames.clear();
        interfaces.clear();
    }

    @Override
    public void onEnable() {
        scriptEngine = new ScriptEngineManager().getEngineByName("JavaScript");

        EventExecutor executor = new ScriptEventExecutor();
        bukkitEvents.stream().forEach(clazz -> {
            eventNames.put(clazz.getSimpleName(), clazz.getCanonicalName());
            getServer().getPluginManager().registerEvent(clazz, new Listener() {
            }, EventPriority.NORMAL, executor, ScriptPlugin.this, false);
        });

        File directory = new File(getDataFolder(), "scripts");
        if (directory.mkdirs()) {
            getLogger().log(Level.INFO, "Created \"scripts\" directory");
        }

        FileFilter fileFilter = pathname -> {
            if (!pathname.isFile() || pathname.getName().charAt(0) == '.') {
                return false;
            }

            String extension = pathname.getName().toLowerCase();
            int index = extension.lastIndexOf('.');
            if (index > -1) {
                extension = extension.substring(index + 1);
            }
            return !extension.equals(pathname.getName()) && extension.equals("js");
        };

        try {
            WatchService service = FileSystems.getDefault().newWatchService();
            Path path = directory.toPath();
            watchKey = path.register(service, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
            Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
                WatchKey watchKey = service.poll();
                if (watchKey != null) {
                    try {
                        watchKey.pollEvents().forEach(event -> {
                            WatchEvent.Kind<?> kind = event.kind();
                            Path changed = (Path) event.context();
                            Path full = path.resolve(changed);
                            File file = full.toFile();
                            if (!fileFilter.accept(file)) {
                                return;
                            }

                            if (kind == StandardWatchEventKinds.ENTRY_CREATE || kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                                loadFile(file);
                            } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                                ScriptInterface scriptInterface = interfaces.remove(full);
                                if (scriptInterface != null) {
                                    scriptInterface.cleanup();
                                    getLogger().log(Level.INFO, "\"{0}\" has been unloaded!", file.getName());
                                }
                            }
                        });
                    } finally {
                        watchKey.reset();
                    }
                }
            }, 100L, 100L);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (File file : directory.listFiles(fileFilter)) {
            loadFile(file);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        onDisable();
        onEnable();
        sender.sendMessage(ChatColor.GREEN + "Congratulations, you made it.");
        return true;
    }

    private void loadFile(File file) {
        ScriptInterface scriptInterface = new ScriptInterface(ScriptPlugin.this);
        Bindings context = scriptEngine.createBindings();
        context.put("__DIRECTORY__", file.getParentFile().toPath().toAbsolutePath().toString().concat(File.separator));
        context.put("manager", scriptInterface);
        context.put("server", getServer());
        context.put("logger", getLogger());
        context.put("events", eventNames);
        context.put("plugin", this);

        Path path = file.toPath();
        try (FileReader reader = new FileReader(file)) {
            scriptEngine.eval(reader, context);
            interfaces.compute(path, (p, old) -> {
                if (old != null) {
                    old.cleanup();
                    getLogger().log(Level.INFO, "\"{0}\" was reloaded!", file.getName());
                }
                return scriptInterface;
            });
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                return;
            }
            getLogger().log(Level.SEVERE, "Failed to load \"{0}\"", file);
            getLogger().log(Level.SEVERE, "", e);
        } catch (ScriptException e) {
            getLogger().log(Level.SEVERE, "An error occurred whilst evaluating {0}", file);
            getLogger().log(Level.SEVERE, "", e);

            if (interfaces.remove(path) != null) {
                getLogger().log(Level.INFO, "\"{0}\" was forcefully unloaded!", file.getName());
            }
        }
    }

    private final class ScriptEventExecutor implements EventExecutor {
        @Override
        public void execute(Listener listener, Event event) throws EventException {
            for (Iterator<Map.Entry<Path, ScriptInterface>> iterator = interfaces.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<Path, ScriptInterface> entry = iterator.next();
                try {
                    entry.getValue().emit(event);
                } catch (ScriptException e) {
                    entry.getValue().cleanup();
                    iterator.remove();
                    throw new EventException(e, e.getMessage());
                }
            }
        }
    }
}
