package Blocks;
/**
 * The unified entity of four blocks put together into a shape.
 * Used to move or rotate all of the blocks togehter.
 *
 * @author T03-2
 */
public class TetrominoModel {
	// Block list consisting of the four blocks a part of a TetrominoModel
	private Block[] blocks;

	// Boolean whether the block is a straight piece or not (Rotation is different)
	private boolean isStraight;

	private boolean falling = true;

	/**
	 * Creates a new TetrominoModel with the given blocks and also sets
	 * whether the block should be treated as if it's straight.
	 *
	 * @param blocks
	 * @param isStraight
	 */
	public TetrominoModel(Block[] blocks, boolean isStraight) {
		this.setBlocks(blocks);
		this.isStraight = isStraight;

		for (Block block : this.blocks) {
			if (block != null) {
				block.setTetromino(this);
			}
		}
	}


	/**
	 * Copy constructor for TetrominoModel
	 *
	 * @param tetromino
	 */
	public TetrominoModel(TetrominoModel tetromino) {
		this.setBlocks(tetromino.getBlocks());
		this.isStraight = tetromino.getIsStraight();

		for (Block block : this.blocks) {
			if (block != null) {
				block.setTetromino(this);
			}
		}
	}

	/**
	 * Returns a shallow copy of the instance's blocks
	 *
	 * @return Block[]
	 */
	public Block[] getBlocks() {
		Block[] returnBlocks = new Block[4];

		for (int i = 0; i < 4; i++) {
			if (this.blocks[i] == null) {
				returnBlocks[i] = null;
			} else {
				returnBlocks[i] = new Block(this.blocks[i]);
			}
		}
		TetrominoModel t = new TetrominoModel(returnBlocks, false);
		return returnBlocks;
	}

	/**
	 * Getter method for the instance variable isStraight
	 * @return boolean
	 */
	public boolean getIsStraight() {
		return this.isStraight;
	}

	/**
	 * Getter method for the instance variable falling
	 * @return boolean
	 */
	public boolean getFalling() {
		return this.falling;
	}

	/**
	 * Sets the list of blocks to be used by this instance
	 */
	public void setBlocks(Block[] blocks) {
		this.blocks = blocks;
	}

	/**
	 * Sets the tetromino's 'center' position to the given x and y. Does so based on the highest block of the tetromino.
	 * (Used for swapping/holding)
	 *
	 * @param newX
	 * @param newY
	 *
	 * @return boolean
	 */
	public boolean setCenterPos(int newX, int newY) {
		Block[] blocks = this.getBlocks();
		int indexForHighestY = 0;
		for (int i = 1; i < 4; i++) {
			if (blocks[i].getPositionY() < blocks[indexForHighestY].getPositionY()) {
				indexForHighestY = i;
			}
		}
		int horDist = newX - blocks[indexForHighestY].getPositionX();
		int verDist = newY - blocks[indexForHighestY].getPositionY();

		return this.move(horDist, verDist);
	}

	/**
	 * Checks whether moving any of the blocks to a given position will have them collide with anything
	 * returning a Block[] with the blocks in the new position or null if a collision was detected.
	 *
	 * @return Block[] 
	 *
	 * @param horDist
	 * @param verDist
	 */
	public Block[] checkCollideMove(int horDist, int verDist) {
		Block[] manipBlocks = this.getBlocks();

		for (int i = 0; i < 4; i++) {
			int newX = manipBlocks[i].getPositionX() + horDist;
			int newY = manipBlocks[i].getPositionY() + verDist;

			if (!(manipBlocks[i].setPositionY(newY))) {
				if (verDist > 0) {
					this.setFalling(false);
				}
				return null;
			}

			if (!(manipBlocks[i].setPositionX(newX))) {
				return null;
			} 
		}

		// Will attempt to move the blocks to the desired positions.
		return manipBlocks;
		
	}

	/**
	 * Moves the instance's blocks a given distance.
	 *
	 * @return boolean
	 *
	 * @param horDist
	 * @param verDist
	 */
	public boolean move(int horDist, int verDist) {
		Block[] outcome = this.checkCollideMove(horDist, verDist);
		if (outcome != null) {
			this.setBlocks(outcome);
			return true;
		}
		return false;
	}

	/**
	 * Moves the instance down until it collides.
	 */
	public void placeTetromino() {
		while (this.move(0, 1));
		this.setFalling(false);
	}

	/**
	 * Checks if the instance's blocks will collide with anything if it is rotated in the given direction.
	 * If there is no collision returns a Block[] where the blocks have been rotated around index zero.
	 * Otherwise null is returned.
	 *
	 * IMPORTANT:
	 * 
	 * Straight block rotation is not implemented yet.
	 *
	 *
	 * @return Block[]
	 *
	 * @param turnClockwise
	 */
	public Block[] checkCollideRotate(boolean turnClockwise) {
		Block[] manipBlocks = this.getBlocks();

		if (this.getIsStraight()) {
			int rotCentX = manipBlocks[0].getPositionX();
			int rotCentY = manipBlocks[0].getPositionY();

			// Set horizontal outside of for loop because block positions end up changing and this condition might become false part way through iteration.
			boolean horizontal = true;
			if (manipBlocks[0].getPositionX() == manipBlocks[1].getPositionX()) {
				horizontal = false;
			}

			// Skip i = 0 because that's the center block (When rotating that block will not move)
			for (int i = 1; i < 4; i++) {
				if (horizontal) {
					// Get Horizontal Displacement
					int displacement = manipBlocks[i].getPositionX() - rotCentX;

					// Set block to respective vertical position
					if (!(manipBlocks[i].setPositionX(rotCentX) && manipBlocks[i].setPositionY(rotCentY + displacement))) {
						return null;
					}
				} else {
					int displacement = manipBlocks[i].getPositionY() - rotCentY;

					if (!(manipBlocks[i].setPositionX(rotCentX + displacement) && manipBlocks[i].setPositionY(rotCentY))) {
						return null;
					}
				}
			}
		// Not a Straight TetrominoModel
		} else {
			// Skip i = 0 because that's the center block (When rotating that block will not move)
			for (int i = 1; i < 4; i++) {
				// Get block x and y relative to the center block of the Tetromino
				int bX = manipBlocks[i].getPositionX() - manipBlocks[0].getPositionX();
				int bY = manipBlocks[i].getPositionY() - manipBlocks[0].getPositionY();
				
				if (turnClockwise) {
					int tempX = bX;
					bX = bY;
					bY = tempX;
				}

				// Corner Piece
				if (bX != 0 && bY != 0) {
					if (bX == bY) {
						bY *= -1;
					} else {
						bX = bY;
					}
				// Edge Piece
				} else {
					if (bX != 0) {
						bY = bX * -1;
						bX = 0;
					} else {
						bX = bY;
						bY = 0;
					}
				}

				if (turnClockwise) {
					int tempX = bX;
					bX = bY;
					bY = tempX;
				}

				// Change x, y back to a proper space on the grid now that they've been rotated around center x, y.
				bX += manipBlocks[0].getPositionX();
				bY += manipBlocks[0].getPositionY();

				if (!(manipBlocks[i].setPositionX(bX) && manipBlocks[i].setPositionY(bY))) {
					return null;
				}
			}
			
		}

		return manipBlocks;
	}

	/**
	 * Rotates the instance in the given direction returning whether it was successful or not.
	 *
	 * @return boolean
	 *
	 * @param turnClockwise
	 */
	public boolean rotate(boolean turnClockwise) {
		Block[] outcome = this.checkCollideRotate(turnClockwise);
		if (outcome != null) {
			this.setBlocks(outcome);
			return true;
		} 
		return false;
	}


	/**
	 * Sets all blocks of this instance to be falling or not 
	 *
	 * @param falling
	 */
	public void setFalling(boolean falling) {
		this.falling = falling;
		for (Block block : this.blocks) {
			block.setFalling(falling);
		}
	}
}