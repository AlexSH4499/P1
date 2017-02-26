
public class VirtualDiskBlock 
{
	
	private static final int DEFAULT_BLOCK_SIZE = 256;
	private int capacity;
	private int blockSize;
	
	public VirtualDiskBlock()
	{
		blockSize = DEFAULT_BLOCK_SIZE;
	}
	
	public VirtualDiskBlock(int blockCapacity)
	{
		blockSize = blockCapacity;
	}
	
	public int getCapacity()
	{
		return capacity;
	}
	
	//My code goes here
	public void setElement(int index, VirtualDiskBlock b)
	{
		//DiskUnit.class.getName().replace(DiskUnit.class.getName(), charAt(index,(char)nuevo));
	}

	public byte getElement(int index)
	{
		byte b8 =0;
		return b8;
	}

	public int getBlockSize() {
		// TODO Auto-generated method stub
		return 0;
	}


}
