package org.geotools.process.vector3d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.geotools.factory.FactoryRegistry;
import org.geotools.process.factory.AnnotatedBeanProcessFactory;
import org.geotools.process.factory.DescribeProcess;
import org.geotools.process.vector3d.AggregateProcess;
import org.geotools.process.vector3d.BoundsProcess;
import org.geotools.process.vector3d.BufferFeatureCollection;
import org.geotools.process.vector3d.CentroidProcess;
import org.geotools.process.vector3d.ClipProcess;
import org.geotools.process.vector3d.CollectGeometries;
import org.geotools.process.vector3d.CountProcess;
import org.geotools.process.vector3d.GridProcess;
import org.geotools.process.vector3d.InclusionFeatureCollection;
import org.geotools.process.vector3d.IntersectionFeatureCollection;
import org.geotools.process.vector3d.NearestProcess;
import org.geotools.process.vector3d.PointBuffers;
import org.geotools.process.vector3d.QueryProcess;
import org.geotools.process.vector3d.RectangularClipProcess;
import org.geotools.process.vector3d.ReprojectProcess;
import org.geotools.process.vector3d.SimplifyProcess;
import org.geotools.process.vector3d.SnapProcess;
import org.geotools.process.vector3d.UnionFeatureCollection;
import org.geotools.process.vector3d.UniqueProcess;
import org.geotools.process.vector3d.VectorZonalStatistics;
import org.geotools.text.Text;

/**
 * Factory providing a number of processes for working with feature data.
 * <p>
 * Internally this factory makes use of the information provided by
 * the {@link DescribeProcess} annotations to produce the correct
 * process description.
 * 
 * @author Jody Garnett (LISAsoft)
 *
 * @source $URL$
 */
public class VectorProcessFactory extends AnnotatedBeanProcessFactory {

    static volatile BeanFactoryRegistry<VectorProcess> registry;

    public static BeanFactoryRegistry<VectorProcess> getRegistry() {
        if (registry == null) {
            synchronized (VectorProcessFactory.class) {
                if (registry == null) {
                    registry = new BeanFactoryRegistry<VectorProcess>(VectorProcess.class);
                }
            }
        }
        return registry;
    }

    public VectorProcessFactory() {
        super(Text.text("Vector processes3D"), "vec3D", getRegistry().lookupBeanClasses());
    }

}
