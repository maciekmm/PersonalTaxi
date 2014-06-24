<h1>PersonalTaxi</h1>

<p><i>Make those damn zombies move you to desired location</i></p>
<p><b>Works best on flat surface</b></p>

<h2> Compiling</h2>
1. Make sure you use **SPIGOT** with bukkit mappings *v1_7_R3*.

<h2> Config</h2>
```YAML
general:
    taxiName: '%s''s taxi' #Name displayed above zombie's head
    item:
        material: COMPASS #Material of taxi item
        name: '&2Taxi' #Name of taxi item
        description:
        - 'Use this item to call your taxi.' #Description of item, you must use § for color codes here
        recipe:
            type: SHAPED #Recipe shape SHAPED or SHAPELESS
            layout: [xxx,xyx,xxx] #Layout for SHAPED recipe
            ingredients:
                x: STONE #ingredients
                y: DIAMOND
                #whatever: STONE #for shapeless recipe
destinations: #Destination list
- world: World #world name
  destinations:
  - x: 0 #x coord
    z: 0 #z coord
    icon: STONE #icon describing destination in icon menu
    name: '&5Park' #name of destination
    permission: personaltaxi.access.destination.park #If no permission is set "personaltaxi.access.default" will be used
  - x: 0
    z: 0
    icon: STONE
    name: '&5Park'
- world: OtherWorld
  destinations:
  - x: 0
    z: 0
    icon: STONE_SWORD
    name: Park
```

<h2>Warning!</h2>
<p>Pathfinding does not work well with destinations >1000 blocks.
It's known bug, and i'll try to fix this later.</p>