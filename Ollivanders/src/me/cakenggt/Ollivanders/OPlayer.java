package me.cakenggt.Ollivanders;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serializable player object
 * @author lownes
 *
 */
public class OPlayer implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3457337288586721283L;
	private Map<Spells, Integer> SpellCount = new HashMap<Spells, Integer>();
	private List<OEffect> effects = new ArrayList<OEffect>();
	//This is the spell loaded into the wand for casting with left click
	private Spells spell;
	private int souls;
	
	public OPlayer(){
		Spells[] spells = Spells.values();
		for (Spells spell : spells){
			SpellCount.put(spell, 0);
		}
		souls = 1;
	}
	
	public Map<Spells, Integer> getSpellCount(){
		return SpellCount;
	}
	
	public void setSpellCount(Map<Spells, Integer> map){
		SpellCount = map;
	}
	
	public Spells getSpell(){
		return spell;
	}
	
	public void setSpell(Spells s){
		spell = s;
	}
	
	public void resetSpellCount(){
		Spells[] spells = Spells.values();
		for (Spells spell : spells){
			SpellCount.put(spell, 0);
		}
	}
	
	public int getSouls(){
		return souls;
	}
	
	public void resetSouls(){
		souls = 1;
	}
	
	public void addSoul(){
		souls ++;
	}
	
	public List<OEffect> getEffects(){
		return effects;
	}
	
	public void addEffect(OEffect e){
		System.out.println("Adding: " + e);
		effects.add(e);
		System.out.println("Added");
	}
	
	public void remEffect(OEffect e){
		effects.remove(e);
	}
	
	public void resetEffects(){
		effects.clear();
	}
}