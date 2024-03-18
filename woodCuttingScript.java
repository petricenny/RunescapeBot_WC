import org.dreambot.api.methods.Animations;
import org.dreambot.api.methods.container.impl.Inventory;
import org.dreambot.api.methods.container.impl.bank.Bank;
import org.dreambot.api.methods.container.impl.bank.BankLocation;
import org.dreambot.api.methods.input.Camera;
import org.dreambot.api.methods.interactive.GameObjects;
import org.dreambot.api.methods.interactive.Players;
import org.dreambot.api.methods.map.Tile;
import org.dreambot.api.methods.walking.impl.Walking;
import org.dreambot.api.script.AbstractScript;
import org.dreambot.api.script.Category;
import org.dreambot.api.script.ScriptManifest;
import org.dreambot.api.utilities.Logger;
import org.dreambot.api.utilities.Sleep;
import org.dreambot.api.wrappers.interactive.GameObject;
import org.dreambot.api.wrappers.interactive.Character;
import org.dreambot.api.wrappers.interactive.Player;

@ScriptManifest(name = "SMWoodCutting", description = "My script description", author = "Episolorious",
        version = 1.2, category = Category.WOODCUTTING, image = "")

public class woodCuttingScript extends AbstractScript
{
    int programState = 0;
    Player myChar = Players.getLocal();
    double randDouble = Math.random();
    int cameraMove = 0;
    Tile woodArea = new Tile(3190,3242);
    Tile rightStairCase = new Tile(3206,3210, 2);
    @Override
    public void onStart()
    {

    }
    @Override
    public int onLoop()
    {
        switch(programState)
        {
            case(0):
                chopWood();
                break;
            case(1):
                bankWood();
                break;
            case(2):
                walkToWood();
                break;
        }
        return 0;
    }

    @Override
    public void onExit()
    {

    }

    public void chopWood()
    {
        if(Inventory.emptySlotCount() != 0 )
        {
            // Inventory is not empty and player is not animating, find a tree
            if(!myChar.isAnimating())
            {
                GameObject tree = GameObjects.closest("Tree");
                // there is a tree in the area
                if(tree != null)
                {
                    tree.interact("Chop down");
                    // add a rng to this
                    Sleep.sleep(2000);
                    cameraMove++;
                    // rotates camera after a random number of trees
                    if(cameraMove == (int)(randDouble*8))
                    {
                        Camera.rotateToEntity(tree);
                        cameraMove = 0;
                        randDouble = Math.random();
                    }
                }
                else
                {
                    Walking.walk(woodArea);
                }
            }
        }
        //inventory full, move to banking
        else
        {
            programState = 1;
            Logger.log("Going to the bank.");
        }
    }

    public void bankWood()
    {
        if(Walking.walk(BankLocation.getNearest()))
        {
            Sleep.sleep(4000);
            GameObject bankBox = GameObjects.closest("Bank booth");
            if(bankBox != null)
            {
                bankBox.interact("bank");
                if(Bank.depositAllExcept("Bronze axe"))
                {
                    // banking complete, move to wood area
                    programState = 2;
                    Logger.log("Going back to woodcutting area.");
                    Sleep.sleep(2000);
                }
            }
        }
    }

    public void walkToWood()
    {
        Walking.walk(rightStairCase);
        Sleep.sleep(4000);
        for(int i = 0; i < 10; i++)
        {
            Walking.walk(woodArea);
            Sleep.sleep(1500);
        }
        programState = 0;
        Logger.log("Cutting trees");
    }
}
