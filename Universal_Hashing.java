//package Assign1;

import java.io.*;
import java.util.*;

public class Universal_Hashing extends Open_Addressing{
	int a;
	int b;
	int p;

	protected Universal_Hashing(int w, int seed) {
		super(w, seed, -1);
		int temp = this.m+1; // m is even, so temp is odd here
		while(!isPrime(temp)) {
			temp += 2;
		}
		this.p = temp;
		a = generateRandom(0, p, seed);
		b = generateRandom(-1, p, seed);
	}
	
	/**
	 * Checks if the input int is prime
	 */
	public static boolean isPrime(int n) {
        if (n <= 1) return false;
        for (int i = 2; i*i <= n; i++) {
        	if (n % i == 0) return false;
        }
        return true;
    }
	
	/**
     * Implements universal hashing
     */
	@Override
    public int probe(int key, int i) {
    	int h = ((a*key + b) % p) % m;
    	int g = (h+i) % power2(r);
		return g;
    }

    /**
     * Inserts key k into hash table. Returns the number of collisions encountered,
     * and resizes the hash table if needed
     */
	@Override
    public int insertKeyResize(int key) {

		float loadFactor = (float) (this.size+1)/m; //size+1 because we are considering the element we are adding

		if (loadFactor>MAX_LOAD_FACTOR){

			//create deep copy of our current table
			int[] copyTable = new int[m];
			for (int i=0;i<m;i++){
				copyTable[i] = this.Table[i];
			}

			//Reinitialize the parameters
			this.w = this.w + 2;
			this.r = (int) (w - 1) / 2 + 1;
			this.m = power2(r);
			this.A = generateRandom((int) power2(w - 1), (int) power2(w), seed);
			this.size = 0;
			int temp = this.m+1; // m is even, so temp is odd here
			while(!isPrime(temp)) {
				temp += 2;
			}
			this.p = temp;
			a = generateRandom(0, p, seed);
			b = generateRandom(-1, p, seed);


			//Reinitialize this.Table
			this.Table = new int[m];
			for (int i = 0; i < m; i++) {
				this.Table[i] = -1;
			}

			for (int i=0;i<copyTable.length;i++){
				if(copyTable[i]==-1) {
					continue; //empty slot
				}
				insertKey(copyTable[i]);
			}
		}

		return insertKey(key);
    }
}
