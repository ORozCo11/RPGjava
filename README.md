Functional Requirements
1. Character System [Inheritance + Encapsulation]

An abstract base class Character (or interface Battler) with fields: name, hp, maxHp, attackPower, defensePower
At minimum three concrete subclasses: Warrior, Mage, and Archer (you may add more for bonus points)
Players can choose at least 2 characters at the beginning for their party and they will control all characters
Each subclass must have its own constructor, a unique passive trait, and a unique special skill
All fields must be accessed only through getters/setters, no public field access


2. Battle System [Polymorphism]

A turn-based combat loop where the player faces 4 enemy waves (at least 4 enemy types, you can mix and match enemies per wave)
Each round: Player chooses from (a) Basic Attack, (b) Use Skill, (c) Use Item, (d) Flee
If players flee, there is a % chance that it will fail and all members will take a certain amount of damage
Basic attacks always has a % chance to hit, miss, or crit and can be modified via skills, items, or taunts
attack() and useSkill() must be overridden in each subclass with different damage formulas per class
Enemy AI performs a randomized action each turn (attack, buff, or taunt)
Taunt has a % chance to be successful
If successful, Players are forced to attack on their next turn and has a high % chance of missing
Battle ends when player HP reaches 0 or all enemies are defeated

3. Inventory & Items [Collections + Encapsulation]

Player starts with 3 consumable items (e.g., Health Potion, Mana Elixir, Revive Scroll)
Items stored in an ArrayList<Item>, using an item removes it from the list
An Item class with name, description, and effect fields are accessed via getters only


4. Exception Handling

Invalid menu selections must be caught with a try-catch block
Using an item when inventory is empty must display a handled error, not a crash
Students must implement at least one custom Exception class (e.g., EmptyInventoryException)



5. User Interface

Game must be fully playable via JFrames with supporting JOptionPanes wherever necessary.
All menus must show current HP, enemy HP, and available actions each turn
Victory and defeat screens must display a final score (enemies defeated, turns taken)


6. Other Features 

Save/Load game state to a text file using File I/O 
Players must be able to continue on the wave they last saved
A shop system between waves with a currency mechanic
Animated ASCII art/GIFs/Images for battle scenes