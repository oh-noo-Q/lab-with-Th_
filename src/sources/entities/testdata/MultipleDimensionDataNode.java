package entities.testdata;


import entities.SpecialCharacter;
import entities.parser.object.VariableNode;

/**
 * Represent variable as two dimension array
 *
 * @author ducanhnguyen
 */
public abstract class MultipleDimensionDataNode extends ArrayDataNode {

    private int dimensions;

    /**
     * The size of variable, [sizes0][sizes1]...[sizesN-1]
     */
    private int[] sizes;

    @Override
    public boolean haveValue() {
        if (sizes[0] <= 0)
            return true;

        return super.haveValue();
    }

    @Override
    public String getInputForGoogleTest() throws Exception {
//        if (isUseUserCode()) {
//            return getUserCodeContent();
//        }

        String declaration = "";

        if (!getChildren().isEmpty()) {

            String coreType = ((ValueDataNode) getChildren().get(0)).getRawType();

            if (isExternel())
                coreType = "";

            StringBuilder dimension = new StringBuilder();
            for (int size : sizes)
                dimension.append("[").append(size).append("]");

            if (isPassingVariable()) {
                declaration = String.format("%s %s%s" + SpecialCharacter.END_OF_STATEMENT, coreType,
                        this.getVituralName(), dimension);
            } else if (isAttribute()) {
                declaration = "";
            } else {
                declaration = String.format("%s %s%s" + SpecialCharacter.END_OF_STATEMENT, coreType,
                        this.getVituralName(), dimension);
            }
        }

        return declaration + super.getInputForGoogleTest();
    }

    public int getDimensions() {
        return dimensions;
    }

    public void setDimensions(int dimensions) {
        this.dimensions = dimensions;
    }

    public int[] getSizes() {
        return sizes;
    }

    public void setSizes(int[] sizes) {
        this.sizes = sizes;
    }

    public int getSizeOfDimension(int dimension) {
        return sizes[dimension];
    }

    public void setSizeOfDimension(int dimension, int size) {
        sizes[dimension] = size;
    }


    @Override
    public void setCorrespondingVar(VariableNode correspondingVar) {
        super.setCorrespondingVar(correspondingVar);
//        if (VariableTypeUtils.isTwoDimension(correspondingVar.getRawType())) {
//            sizeB = correspondingVar.getSizeOfArray();
//        }
//        INode type = ResolveCoreTypeHelper.resolve(correspondingVar);
//        super.setCorrespondingType(type);
    }

    @Override
    public MultipleDimensionDataNode clone() {
        MultipleDimensionDataNode clone = (MultipleDimensionDataNode) super.clone();

        clone.dimensions = dimensions;
        clone.sizes = new int[dimensions];

        for (int i = 1; i < dimensions; i++)
            clone.sizes[i] = sizes[i];

        if (isFixedSize()) {
            clone.sizes[0] = sizes[0];

//            try {
//                new TreeExpander().expandTree(clone);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }

        return clone;
    }
}
