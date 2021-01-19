import numpy as np
import tensorflow as tf
import trimesh

import tensorflow_graphics.geometry.transformation as tfg_transformation
from tensorflow_graphics.notebooks import threejs_visualization


!wget -N https://free3d.com/dl-files.php?p=5aa51b3626be8b97658b4567&f=1
mesh = trimesh.load("Sting-Sword-lowpoly.obj")
mesh = {"vertices": mesh.vertices, "faces": mesh.faces}
_ = threejs_visualization.triangular_mesh_renderer(mesh, width=400, height=400)

axis = np.array((0., 1., 0.))  # y axis.
angle = np.array((np.pi / 4.,))  # 45 degree angle.

mesh["vertices"] = tfg_transformation.axis_angle.rotate(mesh["vertices"], axis,
                                                        angle).numpy()
_ = threejs_visualization.triangular_mesh_renderer(mesh, width=400, height=400)
