//package Assign1;

import java.util.*;

public class Open_Addressing {
	public static final double MAX_LOAD_FACTOR = 0.75;
	
	public int m; // number of slots
	public int A; // the default random number
	int w;
	int r;
	int seed;
	public int[] Table;
	int size; // number of elements stored in the hash table

	protected Open_Addressing(int w, int seed, int A) {
		this.seed = seed;
		this.w = w;
		this.r = (int) (w - 1) / 2 + 1;
		this.m = power2(r);
		if (A == -1) {
			this.A = generateRandom((int) power2(w - 1), (int) power2(w), seed);
		} else {
			this.A = A;
		}
		this.Table = new int[m];
		for (int i = 0; i < m; i++) {
			Table[i] = -1;
		}
		this.size = 0;
	}

	/**
	 * Calculate 2^w
	 */
	public static int power2(int w) {
		return (int) Math.pow(2, w);
	}

	public static int generateRandom(int min, int max, int seed) {
		Random generator = new Random();
		if (seed >= 0) {
			generator.setSeed(seed);
		}
		int i = generator.nextInt(max - min - 1);
		return i + min + 1;
	}

	/**
	 * Implements the hash function g(k)
	 */
	public int probe(int key, int i) {
		Chaining o = new Chaining(w,seed,A);
		return (o.chain(key)+i)%power2(r);
	}

	/**
	 * Inserts key k into hash table. Returns the number of collisions encountered
	 */
	public int insertKey(int key) {
		int nbOfCollisions = 0;
		int i=0;
		boolean slotFound = false;

		while (!slotFound && i!=m){
			if (this.Table[probe(key,i)]==-1){
				this.Table[probe(key,i)] = key;
				this.size++;
				slotFound = true;
			}else{
				i++;
				nbOfCollisions++;
			}
		}
		return nbOfCollisions;
	}


	/**
	 * Sequentially inserts a list of keys into the HashTable. Outputs total number of collisions
	 */
	public int insertKeyArray(int[] keyArray) {
		int collision = 0;
		for (int key : keyArray) {
			collision += insertKey(key);
		}
		return collision;
	}

	/**
	 /*@param the key k to be searched
	 * @return an int array containing 2 elements:
	 * first element = index of k in this.Table if the key is present, = -1 if not present
	 * second element = number of collisions occurred during the search
	 */
	public int[] searchKey(int k) {
		int nbSlotsVisited = 0;
		int index = -1;

		for(int i=0;i<m;i++){
			if(Table[probe(k,i)]==k){
				index = i;
				break;
			}
			nbSlotsVisited++;
		}

		int[] output = {index, nbSlotsVisited};
		return output;
	}
	
	/**
	 * Removes key k from hash table. Returns the number of collisions encountered
	 */
	public int removeKey(int k){
		int[] output = searchKey(k);

		if (output[0]!=-1) {
			Table[output[0]] = -1;
			this.size -= 1;
		}

		return output[1];
	}

	/**
	 * Inserts key k into hash table. Returns the number of collisions encountered,
	 * and resizes the hash table if needed
	 */
	public int insertKeyResize(int key) {

		float loadFactor = (float) (this.size+1)/m; //size+1 because we are considering the element we are adding

		if (loadFactor>MAX_LOAD_FACTOR){

			//create deep copy of our current table
			int[] copyTable = new int[m];
			for (int i=0;i<m;i++){
				copyTable[i] = this.Table[i];
			}

			//Reinitialize all the parameters
			this.w = this.w + 2;
			this.r = (int) (w - 1) / 2 + 1;
			this.m = power2(r);
			this.A = generateRandom((int) power2(w - 1), (int) power2(w), seed);
			this.size = 0;

			//Reinitialize this.Table
			this.Table = new int[m];
			for (int i = 0; i < m; i++) {
				this.Table[i] = -1;
			}

			//Inserting elements from copyTable to this.Table using our probe function
			for (int i=0;i<copyTable.length;i++){
				if(copyTable[i] != -1) {
					insertKey(copyTable[i]); //empty slot
				}
			}
		}
		return insertKey(key);
	}

	/**
	 * Sequentially inserts a list of keys into the HashTable, and resize the hash table
	 * if needed. Outputs total number of collisions
	 */
	public int insertKeyArrayResize(int[] keyArray) {
		int collision = 0;
		for (int key : keyArray) {
			collision += insertKeyResize(key);
		}
		return collision;
	}

	/**
	 /* @param the key k to be searched (and relocated if needed)
	 * @return an int array containing 2 elements:
	 * first element = index of k in this.Table (after the relocation) if the key is present, 
	 * 				 = -1 if not present
	 * second element = number of collisions occured during the search
	 */
	public int[] searchKeyOptimized(int k) {

		int[] output = searchKey(k); //0: index, 1: number of slots visited before optimization
		if (output[0] != -1)//Check if the key is in the table
		{
			for (int i = 0; i < this.size; i++) {

				//Check if on the way we find an empty slot
				if (this.Table[probe(k, i)] == -1) {
					this.Table[probe(k, i)] = k; //fill the empty slot
					this.Table[output[0]] = -1; //remove k from original slot
					break;
				}
				if(this.Table[probe(k, i)] == k) {break;}
			}
		}
		return output;
	}

	/**
	 * @return an int array of n keys that would collide with key k
	 */
	public int[] collidingKeys(int k, int n, int w) {
		int[] output = new int[n];
		for (int i=0;i<n;i++){
			output[i] = k + i*power2(w);
		}
		return output;
	}
}
