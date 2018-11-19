import javabridge
import bioformats
import glob, os
import os
from PIL import Image
import sys

javabridge.start_vm(class_path=bioformats.JARS)

#path_np2 = "/project/hackathon/hackers08/shared/data/raw/nd2/high/"
path_np2 = sys.argv[1]
out_dir = sys.argv[2]

# getSeriesCount
# setSeries
# getSeriesT
def gen_dir( file_d ):
   try:
       os.stat( file_d )
   except:
       print( "creating " + file_d )
       os.mkdir( file_d )

for root, dirs, files in os.walk(path_np2):
    for file in files:
        if file.endswith(".nd2"):
             nd2_file =  os.path.join(root, file)
             print( "processing " + nd2_file )
             with bioformats.ImageReader( nd2_file ) as reader:
                n_series = reader.rdr.getSeriesCount()
                for s in range(n_series):
                   file_d = "{}_series{}".format( nd2_file.split("/")[-2], s )
                   gen_dir( os.path.join( out_dir, file_d ) )
                   reader.rdr.setSeries( s )
                   for i in range( reader.rdr.getSizeT() ):
                       img = reader.read( t=i, series=s )
                       to_save = Image.fromarray( (img[:,:,1]*255).astype('uint8'), mode='L' )
                       save_dir = os.path.join( out_dir, file_d )
                       file_name = "{}/{}_series{}_frame{}.tif".format( save_dir, file[:-4], s, i )
                       print( file_name )
                       to_save.save( file_name )

javabridge.kill_vm()
