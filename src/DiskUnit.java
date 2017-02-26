import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.InvalidParameterException;

public class DiskUnit 
{
	private static final int DEFAULT_CAPACITY = 1024; //default number
	private static final int DEFAULT_BLOCK_SIZE = 256; //default number
	
	private int capacity;
	private int blockSize;
	
	private RandomAccessFile disk;
	
	private DiskUnit(String name)
	{
		try{
			disk = new RandomAccessFile(name,"rw");
		}catch (IOException e){
			System.err.println("Unable to start the disk");
			System.exit(1);
		}
	}
	
	public void shutdown()
	{
		try{
			disk.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void write(int blockNum, VirtualDiskBlock b)throws InvalidBlockNumberException,NonExistingDiskException, IOException
	{
		
			if(blockNum > DEFAULT_CAPACITY || blockNum < 0)
			{
				throw new InvalidBlockNumberException("Invalid Block Number");
			}
			else{
				  if(b == null || b.getBlockSize() > blockSize){
					  throw new InvalidBlockNumberException("Invalid Block Number");
				  }
				  else{
					  
					  try{
						  disk.seek(blockNum);
						  //disk.write(b); have to figure out the way we can write a VirtualDiskBlock using the write method
					  }catch(IOException e)
					  {
						  e.printStackTrace();
					  }
					  shutdown();
				  }

			}
		
	}
	
	public void read(int blockNum, VirtualDiskBlock b)throws InvalidBlockNumberException
	{
		if(blockNum > DEFAULT_CAPACITY || blockNum < 0)
		{
			throw new InvalidBlockNumberException("Invalid Block Number");
		}
		else{
			  if(b == null || b.getBlockSize() > blockSize){
				  throw new InvalidBlockNumberException("Invalid Block Number");
			  }
			  else{
				  try {
					disk.seek(blockNum);
					//disk.read((b);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 System.out.println(b.getCapacity());//prints out to console
			  }

		}
	}
	
	public int getCapacity()
	{
		return capacity;
	}
	
	  public int getBlockSize(){
			return blockSize;
			  
		  }
		  public void lowLevelFormat(DiskUnit name) throws NonExistingDiskException
		  {
			  //In this formatting method
			  //We visit each block	except block 0 
			  //and we assign 0's to each block
			  
			  //we have to mount the drive first
			  try{
				  
				  //Convert the DiskUnit's variable to a string 
				  // So we can mount it onto the system
				  mount(name.toString());
				  for(int i = 1; i <= name.getBlockSize(); i++)//block size may be the incorrect parameter
				  {
					  //We seek for block(i) until the limit is reached
					  name.disk.seek(i);
					  //We write 0's into the blocks
					  name.disk.write(0);
				  }
				  //After successful execution, close the file
				 shutdown();
				 
			  }catch(IOException e){
				  e.printStackTrace();
			  }
			  
			  
			
		  }

		  /**
		   * Turns on an existing disk unit whose name is given. If successful, it makes
		   * the particular disk unit available for operations suitable for a disk unit.
		   * @param name the name of the disk unit to activate
		   * @return the corresponding DiskUnit object
		   * @throws NonExistingDiskException whenever no
		   *    ¨disk¨ with the specified name is found.
		  */
		  public static DiskUnit mount(String name)
		  throws NonExistingDiskException
		  {
		     File file=new File(name);
		     if (!file.exists())
		         throw new NonExistingDiskException("No disk has name : " + name);
		    
		     DiskUnit dUnit = new DiskUnit(name);
		     	
		     // get the capacity and the block size of the disk from the file
		     // representing the disk
		     try {
		    	 dUnit.disk.seek(0);
		         dUnit.capacity = dUnit.disk.readInt();
		         dUnit.blockSize = dUnit.disk.readInt();
		     } catch (IOException e) {
		    	 e.printStackTrace();
		     }
		     	
		     return dUnit;     	
		  }
		     
		  /***
		   * Creates a new disk unit with the given name. The disk is formatted
		   * as having default capacity (number of blocks), each of default
		   * size (number of bytes). Those values are: DEFAULT_CAPACITY and
		   * DEFAULT_BLOCK_SIZE. The created disk is left as in off mode.
		   * @param name the name of the file that is to represent the disk.
		   * @throws ExistingDiskException whenever the name attempted is
		   * already in use.
		  */
		  public static void createDiskUnit(String name)
		  throws ExistingDiskException
		  {
		      createDiskUnit(name, DEFAULT_CAPACITY, DEFAULT_BLOCK_SIZE);
		  }
		     
		  /**
		   * Creates a new disk unit with the given name. The disk is formatted
		   * as with the specified capacity (number of blocks), each of specified
		   * size (number of bytes).  The created disk is left as in off mode.
		   * @param name the name of the file that is to represent the disk.
		   * @param capacity number of blocks in the new disk
		   * @param blockSize size per block in the new disk
		   * @throws ExistingDiskException whenever the name attempted is
		   * already in use.
		   * @throws InvalidParameterException whenever the values for capacity
		   *  or blockSize are not valid according to the specifications
		  */
		  public static void createDiskUnit(String name, int capacity, int blockSize)
		  throws ExistingDiskException, InvalidParameterException
		  {
		      File file=new File(name);
		      if (file.exists())
		         throw new ExistingDiskException("Disk name is already used: " + name);
		     	
		      RandomAccessFile disk = null;
		      if (capacity < 0 || blockSize < 0 ||
		           !Utils.powerOf2(capacity) || !Utils.powerOf2(blockSize))
		         throw new InvalidParameterException("Invalid values: " +
		     		   " capacity = " + capacity + " block size = " +
		     		   blockSize);
		      // disk parameters are valid... hence create the file to represent the
		      // disk unit.
		      try {
		          disk = new RandomAccessFile(name, "rw");
		      }
		      catch (IOException e) {
		          System.err.println ("Unable to start the disk");
		          System.exit(1);
		      }

		      reserveDiskSpace(disk, capacity, blockSize);
		     	
		      // after creation, just leave it in shutdown mode - just
		      // close the corresponding file
		      try {
		          disk.close();
		      } catch (IOException e) {
		          e.printStackTrace();
		      }
		  }
		  private static void reserveDiskSpace(RandomAccessFile disk, int capacity,int blockSize){
			  try {
				  disk.setLength(blockSize * capacity);
			  } catch (IOException e) {
				  e.printStackTrace();
			  }

		// write disk parameters (number of blocks, bytes per block) in
		// block 0 of disk space
			  try {
				  disk.seek(0);
				  disk.writeInt(capacity);  
				  disk.writeInt(blockSize);
			  } catch (IOException e) {
				  e.printStackTrace();
			  } 	
		  }

	
}
