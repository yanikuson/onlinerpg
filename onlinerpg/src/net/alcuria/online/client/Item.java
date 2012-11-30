package net.alcuria.online.client;

public class Item {

	public int id;
	public int icon;
	public int type;
	
	public int atk, matk, def, mdef, speed, jump, kb;
	
	public String name;					// name of item
	public String description;			// description of item
	public String visualName;			// url to visual equipment sprite
	public int cost;					// cost of the item
	
	public static final int ID_BLANK = 0;
	public static final int ID_POTION = 1; 
	public static final int ID_SPEED_PILL = 10;
	public static final int ID_WOOD_SWORD = 20;
	public static final int ID_STARLESS_UMBRA = 39;
	public static final int ID_WOOD_ARMOR = 40;
	public static final int ID_WOOD_HELM = 60;
	public static final int ID_WIZARD_HAT = 61;
	public static final int ID_LEATHER_BOOTS = 80;
	
	public static final int TYPE_CONSUMABLE = 1;
	public static final int TYPE_WEAPON = 2;
	public static final int TYPE_ARMOR = 3;
	public static final int TYPE_HELM = 4;
	public static final int TYPE_OTHER = 5;
	public static final int TYPE_RARE = 10;
	
	public Item(int id){
		
		this.id = id;
		this.visualName = "sprites/equips/empty.png";
		this.cost = 1;
		
		switch (id) {
		
		case ID_BLANK:
			this.icon = 0;
			this.type = TYPE_RARE;
			this.name = "";
			this.description = "";
			break;
			
		case ID_POTION:
			this.icon = 1;
			this.type = TYPE_CONSUMABLE;
			this.name = "Potion";
			this.description = "Recovers 50 HP";
			this.cost = 20;
			break;
			
		case ID_SPEED_PILL:
			this.icon = 6;
			this.type = TYPE_CONSUMABLE;
			this.name = "Speed Pill";
			this.description = "Walk speed increased for one minute";
			this.cost = 50;
			break;
			
		case ID_WOOD_SWORD:
			this.icon = 2;
			this.type = TYPE_WEAPON;
			this.name = "Wooden Sword";
			this.description = "ATK+10";
			this.atk = 10;
			this.visualName = "sprites/equips/weapons/1.png";
			this.cost = 100;
			break;
			
		case ID_STARLESS_UMBRA:
			this.icon = 2;
			this.type = TYPE_WEAPON;
			this.name = "Starless Umbra";
			this.description = "IT'S REALLY STRONG! ATK+1000";
			this.atk = 1000;
			break;
			
		case ID_WOOD_ARMOR:
			this.icon = 3;
			this.type = TYPE_ARMOR;
			this.name = "Wooden Armor";
			this.description = "DEF+2";
			this.def = 2;
			this.cost = 120;
			break;
			
		case ID_WOOD_HELM:
			this.icon = 4;
			this.type = TYPE_HELM;
			this.name = "Wooden Helm";
			this.description = "DEF+2";
			this.def = 2;
			this.cost = 80;
			break;
			
		case ID_WIZARD_HAT:
			this.icon = 4;
			this.type = TYPE_HELM;
			this.name = "Wizard Hat";
			this.description = "How magical! MATK+10";
			this.matk = 10;
			this.cost = 75;
			break;
			
		case ID_LEATHER_BOOTS:
			this.icon = 5;
			this.type = TYPE_OTHER;
			this.name = "Leather Boots";
			this.description = "JUMP+5";
			this.jump = 5;
			this.speed = 0;
			break;
			
		default:
			this.atk = 0;
			this.matk = 0;
			this.def = 0;
			this.mdef = 0;
			this.speed = 0;
			this.jump = 0;
			this.kb = 0;
			this.icon = 0;
			this.type = 0;
			this.name = "null item";
			this.description = "null description";
		}
		
	}
}
