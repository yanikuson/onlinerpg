package net.alcuria.online.client;

import com.badlogic.gdx.utils.Array;

public class ItemManager {

	private Array<Item> items;
	public int money = 0;
	
	private int acquisitionID = 0;				// we increment this every time we get a new item in order to sort by "NEW"
	private static final int MAX_ITEMS = 24;	// max number of items we can hold in the player's inventory
	private int[] equipStats;
	
	public ItemManager(){
		items = new Array<Item>(true, MAX_ITEMS);
		
		equipStats = new int[7];
		for (int i = 0; i < equipStats.length; i++) {
			equipStats[i] = 0;
		}
	}

	public void add(int id){

		if (items.size < MAX_ITEMS){
			items.add(new Item(id));
			acquisitionID++;
		}

	}

	public void remove(Item item){

		items.removeValue(item, true);

	}

	public int getSize() {

		return items.size;
	}

	public Item getItem(int id){
		
		return items.get(id);
		
	}
	
	public String getItemName(int id){

		return items.get(id).name;

	}

	public int getItemCost(int id) {
		if (id < items.size && id >= 0){
			return items.get(id).cost;
		}
		return 0;
	}
	
	public String getItemDesc(int id){

		if (id < items.size && id >= 0){
			return items.get(id).description;
		}
		return "";
		
	}

	public int getItemType(int id) {
		
		if (id < items.size && id >= 0){
			return items.get(id).type;
		}
		return -1;
	}

	public void addItem(Item swapItem) {
		if (swapItem != null && items.size < MAX_ITEMS){
			items.add(swapItem);
			acquisitionID ++;
		}
		
	}

	public Item removeIndex(int i) {
		return items.removeIndex(i);
		
	}

	public int[] getItemStats(int index) {
		
		if (index < items.size && index >= 0){
			
			// set all the stat values and return it
			equipStats[0] = items.get(index).atk;
			equipStats[1] = items.get(index).def;
			equipStats[2] = items.get(index).matk;
			equipStats[3] = items.get(index).mdef;
			equipStats[4] = items.get(index).kb;
			equipStats[5] = items.get(index).speed;
			equipStats[6] = items.get(index).jump;
			
			return equipStats;
		}
		
		// if no item is found, we return all zeroes
		for (int i = 0; i < equipStats.length; i++) {
			equipStats[i] = 0;
		}
		return equipStats;
	}
	
	public int getTotalItemsAcquired(){
		return acquisitionID;
	}
}
